self.addEventListener('activate', event => event.waitUntil(clients.claim()));

self.addEventListener('push', event => event.waitUntil(handlePushEvent(event)));

self.addEventListener('notificationclick', event => event.waitUntil(handleNotificationClick(event)));

self.addEventListener('notificationclose', event => console.info('notificationclose event fired'));

var urlToOpenProfile = new URL('/', self.location.origin).href;

async function handlePushEvent(event) {
    console.info('push event emitted');

    const needToShow = await needToShowNotification();
    const dataCache = await caches.open('data');

    if (event.data) {
        console.info('notification received');

        const msg = event.data.json();

        urlToOpenProfile = new URL('/profile/' + msg.title, self.location.origin).href;

        if (needToShow) {
            self.registration.showNotification({
                body: msg.body
            });
        }
        await dataCache.put('message', new Response(msg.body));
    }

    const allClients = await clients.matchAll({ includeUncontrolled: true });
    for (const client of allClients) {
        client.postMessage('data-updated');
    }
}

async function handleNotificationClick(event) {

    let openClient = null;
    const allClients = await clients.matchAll({ includeUncontrolled: true, type: 'window' });
    for (const client of allClients) {
        if (client.url === urlToOpenProfile) {
            openClient = client;
            break;
        }
    }

    if (openClient) {
        await openClient.focus();
    } else {
        await clients.openWindow(urlToOpenProfile);
    }

    event.notification.close();
}

async function needToShowNotification() {
    const allClients = await clients.matchAll({ includeUncontrolled: true });
    for (const client of allClients) {
        return true;
    }
}