**Description:**
- Users can create and manage a list of alarms.
- Notification permissions are implemented.
- A one-time `WorkManager` is used to ensure only one alarm can be active at a time.
- Users cannot set multiple alarms for the same time.
- Users can delete individual alarms or clear all alarms.
- Notifications are configured with vibration and sound, using the system alarm tone by default.
- Snooze is set to 10 seconds for testing.
- If the set alarm time for today has already passed (e.g., user sets an alarm for 7:26 and it’s currently 7:26), the alarm will ring the next day at the same time.
