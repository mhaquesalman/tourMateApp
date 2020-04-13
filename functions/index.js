'user-strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()

exports.sendNotification = functions.database.ref('users/{user_id}/notifications/{notification_id}').onWrite((chagne, context) => {

    //receiver id
    const user_id = context.params.user_id;

    //notification id
    const notification_id = context.params.notification_id;
    console.log("User Id: " + user_id + " Notification Id: " + notification_id);

    const deviceRef = admin.database().ref().child('users').child(user_id).child('notifications').child(notification_id).once('value');
    return deviceRef.then(queryResult => {

        //sender id
        const from_userId = queryResult.val().from;
        //sender message
        const from_message = queryResult.val().message;

        //sender reference
        const from_data = admin.database().ref("users/" + from_userId).once('value');

        //receiver reference
        const to_data = admin.database().ref("users/" + user_id).once('value');

        return Promise.all([from_data, to_data]).then(result => {

            const from_name = result[0].val().fullname;
            const to_name = result[1].val().fullname;
            const to_token = result[1].val().tokenId;

            console.log("From: " + from_name + " To: " + to_name);

            const payload = {
                notification: {
                    title: "Notification From : " + from_name,
                    body: from_message,
                    icon: "default",
                    click_action: "com.salman.tourmateapp.TARGETNOTIFICATION"

                },
                data: {
                    message: from_message,
                    from_user_id: from_userId
                }
            };

            return admin.messaging().sendToDevice(to_token, payload).then(result => {
                console.log("Notification Sent");
                return result;
            });

        });

    });

});
