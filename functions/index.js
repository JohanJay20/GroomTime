const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.updateUserRole = functions.database
    .ref('/admin/updateClaims/{userId}')
    .onCreate(async (snapshot, context) => {
        const userId = context.params.userId;
        const claims = snapshot.val();
        
        try {
            await admin.auth().setCustomUserClaims(userId, claims);
            // Clean up the update request
            await snapshot.ref.remove();
            return null;
        } catch (error) {
            console.error('Error updating user claims:', error);
            throw error;
        }
    }); 