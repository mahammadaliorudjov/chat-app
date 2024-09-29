'use strict';

const chatPage = document.querySelector('#chat-page');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const userSearchInput = document.querySelector('#userSearchInput');

let stompClient = null;
let nickname = null;
let selectedUserId = null;
let pingInterval;

document.addEventListener('DOMContentLoaded', function() {
    nickname = sessionStorage.getItem('username');
    if (!nickname) {
        const usernameElement = document.getElementById('username');
        if (usernameElement) {
            nickname = usernameElement.textContent;
            sessionStorage.setItem('username', nickname);
        }
    }
    let sessionId = sessionStorage.getItem('sessionId');
    if (!sessionId) {
        sessionId = 'sess-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);
        sessionStorage.setItem('sessionId', sessionId);
        fetch('/setSessionId', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Session-ID': sessionId,
                'X-Username': nickname
            }
        });
    }
    sendUserStatus(nickname, 'online');
    connect(nickname, sessionId);
});

function connect(nickname, sessionId) {
    if (nickname) {
        const socket = new SockJS('/ep');
        stompClient = Stomp.over(socket);
        stompClient.connect({ 'X-Session-ID': sessionId, 'X-Username': nickname }, () => {
            onConnected(nickname, sessionId);
            sendUserStatus(nickname, 'online');
        }, onError);
    }
    event.preventDefault();
}

function onConnected(nickname, sessionId) {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    stompClient.subscribe('/topic/user-status', function (message) {
        findAndDisplayUsers();
    });
    document.querySelector('#connected-user-nickname').textContent = nickname;
    findAndDisplayUsers();
}

function sendUserStatus(nickname, status) {
    if (stompClient) {
        const sessionId = sessionStorage.getItem('sessionId');
        stompClient.send('/app/user-status', {}, JSON.stringify({
            username: nickname,
            status: status,
            sessionId: sessionId
        }));
    }
}

window.addEventListener('load', function() {
    pingInterval = setInterval(function() {
        sendUserPing();
    }, 5000);
});

function sendUserPing() {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/user.ping', {}, JSON.stringify({
            username: nickname,
            sessionId: sessionStorage.getItem('sessionId')
        }));
    }
}

async function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    await findAndDisplayUsers();
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content, message.timestamp);
        chatArea.scrollTop = chatArea.scrollHeight;
    }
    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }
    const notifiedUser = document.querySelector(`#${message.senderId}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

function onError() {
    sessionStorage.clear();
}

async function findAndDisplayUsers() {
    const allUsersResponse = await fetch('/allUsers');
    const connectedUsersResponse = await fetch('/users');

    let allUsers = await allUsersResponse.json();
    let connectedUsers = await connectedUsersResponse.json();

    allUsers = allUsers.filter(user => user.username !== nickname);

    const usersList = document.getElementById('usersList');
    usersList.innerHTML = '';

    if (allUsers.length === 0) {
        document.getElementById('no-users-message').classList.remove('hidden');
    } else {
        document.getElementById('no-users-message').classList.add('hidden');
        allUsers.forEach(user => {
            const isOnline = connectedUsers.some(connectedUser => connectedUser.username === user.username);
            appendUserElement(user.username, usersList, isOnline);
        });
    }
}

function appendUserElement(username, usersList, isOnline) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = username;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = username;

    const indicatorsContainer = document.createElement('div');
    indicatorsContainer.style.display = 'flex';
    indicatorsContainer.style.alignItems = 'center';

    if (isOnline) {
        const onlineSpan = document.createElement('div');
        onlineSpan.classList.add('online-status');
        indicatorsContainer.appendChild(onlineSpan);
    }

    const receivedMessages = document.createElement('span');
    receivedMessages.classList.add('nbr-msg', 'hidden');
    indicatorsContainer.appendChild(receivedMessages);

    listItem.appendChild(usernameSpan);
    listItem.appendChild(indicatorsContainer);

    listItem.addEventListener('click', userItemClick);

    usersList.appendChild(listItem);
}


function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');
    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content, chat.timestamp);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}

function displayMessage(senderId, content, timestamp) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if(senderId == nickname) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;

    const time = document.createElement('span');
    time.classList.add('timestamp');
    const messageTime = new Date(timestamp);
    const formattedTime = `${messageTime.getHours()}:${('0' + messageTime.getMinutes()).slice(-2)}`;
    time.textContent = formattedTime;
    if (senderId === nickname) {
        time.classList.add('timestamp-sender');
    }
    message.appendChild(time);
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    const sessionId = sessionStorage.getItem('sessionId');
    if(messageContent && stompClient) {
        const chatMessage = {
            senderId: nickname,
            recipientId: selectedUserId,
            content: messageContent,
            timestamp: new Date().toISOString(),
            sessionId: sessionId
        };
        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageContent, chatMessage.timestamp);
        chatArea.scrollTop = chatArea.scrollHeight;
    }
    messageInput.value = '';
    event.preventDefault();
}

function onLogout() {
    if (stompClient) {
        const sessionId = sessionStorage.getItem('sessionId');
        sendUserStatus(nickname, 'offline');
    }
    window.location.href = '/login';
}

messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);

userSearchInput.addEventListener('input', function() {
    const searchQuery = userSearchInput.value.toLowerCase();
    findAndDisplayUsers().then(() => {
        const usersList = document.getElementById('usersList');
        const userItems = Array.from(usersList.getElementsByClassName('user-item'));

        userItems.forEach(userItem => {
            const username = userItem.id.toLowerCase();
            if (username.includes(searchQuery)) {
                userItem.style.display = '';
            } else {
                userItem.style.display = 'none';
            }
        });
    });
});
