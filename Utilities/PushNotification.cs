using FirebaseAdmin.Messaging;

namespace SmartAlerts.API.Utilities
{
    public class PushNotification
    {
        public static async Task<string> SendNotification(string notificationBody, string deviceToken, int categoryId)
        {
            if (string.IsNullOrWhiteSpace(deviceToken))
                return "Invalid device token";

            try
            {
                var message = new Message
                {
                    Token = deviceToken,
                    Apns = new ApnsConfig
                    {
                        Aps = new Aps
                        {
                            Category = categoryId.ToString(),
                            Alert = new ApsAlert { Title = "smart alert", Body = notificationBody },
                            Badge = 1,
                            Sound = "default"
                        }
                    },
                    Android = new AndroidConfig
                    {
                        Notification = new AndroidNotification
                        {
                            Body = notificationBody,
                            Title = "smart alert",
                            Sound = "default"
                        },
                        Priority = Priority.High
                    }
                };

                var response = await FirebaseMessaging.DefaultInstance.SendAsync(message);
                return response; // This is a message ID string if successful
            }
            catch (FirebaseMessagingException fbm)
            {
                return "Sending push notification failed: " + fbm.Message;
            }
            catch (Exception ex)
            {
                return "Unexpected error: " + ex.Message;
            }
        }
    }
}
