if ("serviceWorker" in navigator) {
  try {
    init();
    
	subscribe().catch(e => {
		if (Notification.permission === 'denied') {
          console.warn('Permission for notifications was denied');
		} else {
    	  console.error('error subscribe(): ' + e);
        }});
  } catch (e) {
    console.error('error init(): ' + e);
  }
}


async function checkSubscription() {
  const registration = await navigator.serviceWorker.ready;
  const subscription = await registration.pushManager.getSubscription();
  if (subscription) {

    const response = await fetch("/isSubscribed", {
      method: 'POST',
      body: JSON.stringify({endpoint: subscription.endpoint}),
      headers: {
        "content-type": "application/json"
      }
    });
    const subscribed = await response.json();
    return subscribed;
    
  }
  return false;
}

async function init() {
  fetch('/publicSigningKey')
     .then(response => response.arrayBuffer())
     .then(key => this.publicSigningKey = key)
     .finally(() => console.info('Application Server Public Key fetched from the server'));

  await navigator.serviceWorker.register("/sw.js", {
    scope: "/"
  }).then(function(registration) {
      console.log('Service worker registration succeeded:', registration)
  });
  
  await navigator.serviceWorker.ready;
  console.info('Service Worker has been installed and is ready');
}

async function unsubscribe() {
  const registration = await navigator.serviceWorker.ready;
  const subscription = await registration.pushManager.getSubscription();
  if (subscription) {
    const successful = await subscription.unsubscribe();
    if (successful) {
      console.info('Unsubscription successful');

      await fetch("/unsubscribeByEndpoint", {
        method: 'POST',
        body: JSON.stringify({endpoint: subscription.endpoint}),
        headers: {
          "content-type": "application/json"
        }
      });

      console.info('Unsubscription info sent to the server');

    }
    else {
      console.error('Unsubscription failed');
    }
  }
}

async function subscribe() {
  const registration = await navigator.serviceWorker.ready;
  var subscription = await registration.pushManager.getSubscription();
  if(subscription == null){
	  subscription = await registration.pushManager.subscribe({
	    userVisibleOnly: true,
	    applicationServerKey: this.publicSigningKey
	});
  }

  console.info(`Subscribed to Push Service: ${subscription.endpoint}`);
  const username = document.getElementById("username").value;
  var jsonSub = JSON.stringify(subscription);
  var sub = JSON.parse(jsonSub);
  sub.username = username;
  await fetch("/subscribe", {
    method: 'POST',
    body: JSON.stringify(sub),
    headers: {
      "content-type": "application/json"
    }
  });

  console.info('Subscription info sent to the server');
}