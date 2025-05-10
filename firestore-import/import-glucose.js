const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

const glucoseLogs = [
  // User 1
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 95.20,
    "log_date": "2025-05-01",
    "log_time": "07:30:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 102.00,
    "log_date": "2025-05-01",
    "log_time": "12:45:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 130.80,
    "log_date": "2025-05-02",
    "log_time": "18:00:00",
    "glucose_status": "High"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 88.90,
    "log_date": "2025-05-03",
    "log_time": "07:20:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 75.50,
    "log_date": "2025-05-03",
    "log_time": "22:00:00",
    "glucose_status": "Low"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 90.00,
    "log_date": "2025-05-04",
    "log_time": "08:00:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 140.30,
    "log_date": "2025-05-04",
    "log_time": "19:00:00",
    "glucose_status": "High"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 85.25,
    "log_date": "2025-05-05",
    "log_time": "06:45:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 65.40,
    "log_date": "2025-05-05",
    "log_time": "23:15:00",
    "glucose_status": "Low"
  },
  {
    "user_id": "NH3jm2mAMdRnwSWraq7a5BTdtNx2",
    "glucose_value": 110.10,
    "log_date": "2025-05-06",
    "log_time": "14:30:00",
    "glucose_status": "Good"
  },

  // User 2
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 80.50,
    "log_date": "2025-05-01",
    "log_time": "08:00:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 60.20,
    "log_date": "2025-05-01",
    "log_time": "21:00:00",
    "glucose_status": "Low"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 100.00,
    "log_date": "2025-05-02",
    "log_time": "13:00:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 120.75,
    "log_date": "2025-05-02",
    "log_time": "18:00:00",
    "glucose_status": "High"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 70.90,
    "log_date": "2025-05-03",
    "log_time": "07:00:00",
    "glucose_status": "Low"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 95.60,
    "log_date": "2025-05-03",
    "log_time": "12:30:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 115.00,
    "log_date": "2025-05-04",
    "log_time": "19:15:00",
    "glucose_status": "High"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 87.75,
    "log_date": "2025-05-05",
    "log_time": "08:00:00",
    "glucose_status": "Good"
  },
  {
    "user_id": "E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 108.30,
    "log_date": "2025-05-05",
    "log_time": "20:45:00",
    "glucose_status": "Good"
  },
  {
    "user_id":"E6mHGi0EquboLYsU4Ac2NMNyOsj1",
    "glucose_value": 67.00,
    "log_date": "2025-05-06",
    "log_time": "22:00:00",
    "glucose_status": "Low"
  }
];

async function importGlucoseLogs() {
  const batch = db.batch();

  glucoseLogs.forEach((log, index) => {
    console.log(`Adding log ${index + 1} for user ${log.user_id}`);
    const docRef = db.collection("glucose_logs").doc(); // Auto-ID
    batch.set(docRef, log);
  });

  try {
    await batch.commit();
    console.log("Glucose logs imported successfully.");
  } catch (error) {
    console.error("Error importing logs:", error);
  }
}

console.log("Starting import...");
importGlucoseLogs();