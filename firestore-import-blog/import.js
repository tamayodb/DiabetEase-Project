const admin = require('firebase-admin');
const fs = require('fs');

// Initialize Firebase Admin
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Sample blog posts (YOUR 5 posts)
const blogPosts = [
  {
    title: "Understanding Type 2 Diabetes",
    content: "Type 2 diabetes is a chronic condition that affects the way your body processes blood sugar. In this article, we explore the causes, symptoms, and preventive measures to manage the disease effectively.",
    excerpt: "Learn what causes Type 2 diabetes and how you can manage or prevent it.",
    author: "Health First Team",
    created_at: admin.firestore.Timestamp.fromDate(new Date("2025-04-28T08:00:00Z")),
    cover_image_url: "",
    tags: ["diabetes", "health", "management"],
    category: "Health",
    reading_time: 5,
    likes_count: 0,
    like_enabled: true
  },
  {
    title: "Top 10 Diet Tips for People with Diabetes",
    content: "Maintaining a healthy diet is essential when living with diabetes. We list the top 10 diet tips, including portion control, choosing low-glycemic foods, and avoiding processed sugars.",
    excerpt: "Discover diet strategies that can help you control diabetes more effectively.",
    author: "Nutrition Experts PH",
    created_at: admin.firestore.Timestamp.fromDate(new Date("2025-04-28T08:10:00Z")),
    cover_image_url: "",
    tags: ["diabetes", "diet", "tips"],
    category: "Nutrition",
    reading_time: 4,
    likes_count: 0,
    like_enabled: true
  },
  {
    title: "Warning Signs of Diabetes You Shouldn't Ignore",
    content: "Early detection of diabetes can make a big difference. This article discusses common symptoms such as excessive thirst, frequent urination, and unexplained weight loss.",
    excerpt: "Spot the early warning signs of diabetes and seek medical help early.",
    author: "Healthy Living Blog",
    created_at: admin.firestore.Timestamp.fromDate(new Date("2025-04-28T08:20:00Z")),
    cover_image_url: "",
    tags: ["diabetes", "symptoms", "early detection"],
    category: "Health",
    reading_time: 3,
    likes_count: 0,
    like_enabled: true
  },
  {
    title: "The Role of Exercise in Managing Diabetes",
    content: "Exercise is a crucial part of diabetes management. This article covers different types of exercises suitable for people with diabetes and how to start a safe fitness routine.",
    excerpt: "Find out how regular exercise can help you manage blood sugar levels.",
    author: "Fitness for All",
    created_at: admin.firestore.Timestamp.fromDate(new Date("2025-04-28T08:30:00Z")),
    cover_image_url: "",
    tags: ["diabetes", "exercise", "tips"],
    category: "Fitness",
    reading_time: 6,
    likes_count: 0,
    like_enabled: true
  },
  {
    title: "Debunking Common Myths About Diabetes",
    content: "There are many myths about diabetes, from what causes it to how it can be treated. We clarify the most common misconceptions and share facts based on scientific research.",
    excerpt: "Separate facts from fiction when it comes to diabetes.",
    author: "Dr. Anna Reyes",
    created_at: admin.firestore.Timestamp.fromDate(new Date("2025-04-28T08:40:00Z")),
    cover_image_url: "",
    tags: ["diabetes", "myths", "facts"],
    category: "Education",
    reading_time: 5,
    likes_count: 0,
    like_enabled: true
  }
];

// Function to import data
async function importData() {
  const batch = db.batch();

  blogPosts.forEach(post => {
    const docRef = db.collection('blogs').doc(); // Auto-generated ID
    batch.set(docRef, post);
  });

  await batch.commit();
  console.log('✅ Blog posts imported successfully!');
}

// Run the import
importData().catch(console.error);
