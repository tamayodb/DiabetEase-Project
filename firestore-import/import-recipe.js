const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

const ingredientsData = [
  // Meat
  { name: "Tofu", category: "Meat", ingred_image_url: "https://static.vecteezy.com/system/resources/previews/025/222/302/non_2x/tofu-cubes-isolated-on-transparent-background-png.png" },
  { name: "Turkey Sausage", category: "Meat", ingred_image_url: "https://static.vecteezy.com/system/resources/thumbnails/045/649/823/small_2x/turkey-sausage-on-transparent-background-png.png" },

  // Vegetables
  { name: "Cauliflower", category: "Vegetable", ingred_image_url: "https://resources.markon.com/sites/default/files/styles/large/public/pi_photos/Cauliflower_Florets_Hero.jpg" },
  { name: "Squash", category: "Vegetable", ingred_image_url: "https://img.freepik.com/free-photo/pumpkin-slice-isolated-white-background_74190-2924.jpg?semt=ais_hybrid&w=740" },
  { name: "Ginger", category: "Vegetable", ingred_image_url: "https://www.pngplay.com/wp-content/uploads/2/Ginger-PNG-Free-File-Download.png" },
  { name: "Bell Pepper", category: "Vegetable", ingred_image_url: "https://thumbs.dreamstime.com/b/closeup-red-bell-pepper-water-droplets-isolated-transparent-backdrop-png-file-258788023.jpg" },
  { name: "Chives", category: "Vegetable", ingred_image_url: "https://png.pngtree.com/png-clipart/20210530/original/pngtree-leek-food-fresh-photography-png-image_6343277.jpg" },
  { name: "Cilantro", category: "Vegetable", ingred_image_url: "https://www.pngplay.com/wp-content/uploads/9/Cilantro-PNG-Background.png" },
  { name: "Fresh Rosemary", category: "Vegetable", ingred_image_url: "https://static.vecteezy.com/system/resources/previews/038/746/720/non_2x/ai-generated-rosemary-plant-in-watercolor-free-png.png" },
  { name: "Serrano Pepper", category: "Vegetable", ingred_image_url: "https://media.istockphoto.com/id/1425559289/photo/whole-and-chopped-green-jalapeno-pepper-isolated-on-white-background.jpg?s=612x612&w=0&k=20&c=2ZfK9rKrP5NfgFI8TG-gIOhGg7CQ7TO2YbSE2AOXSUw=" },

  // Other
  { name: "Black Pepper", category: "Other", ingred_image_url: "https://img.freepik.com/free-psd/black-pepper-isolated-transparent-background_191095-12631.jpg?semt=ais_hybrid&w=740" },
  { name: "Salt", category: "Other", ingred_image_url: "https://static.vecteezy.com/system/resources/previews/047/087/042/non_2x/salt-in-a-bowl-on-transparent-background-free-png.png" },
  { name: "Olive Oil", category: "Other", ingred_image_url: "https://toppng.com/uploads/preview/olive-oil-11528319801mswpjo6s6m.png" },
  { name: "Poblano Chile Pepper", category: "Other", ingred_image_url: "https://media.istockphoto.com/id/172172234/photo/poblano-chile-pepper-on-a-white-background.jpg?s=612x612&w=0&k=20&c=3s7S3QPaimoYEYVpsyJHqPE3AEiMu9OKSMoLyWEzBdA=" },
  { name: "Plain Nonfat Greek yogurt", category: "Other", ingred_image_url: "https://i5.walmartimages.com/asr/9bdd02d9-352a-4eb4-9de4-402320349b9c.dc23a6a4a7e5edf90d06876f7b883103.jpeg" },
  { name: "Ground Cumin", category: "Other", ingred_image_url: "https://laz-img-sg.alicdn.com/p/fe1aafcda9511e97e04d97366a559fb2.jpg" },
  { name: "Kosher Salt", category: "Other", ingred_image_url: "https://images-na.ssl-images-amazon.com/images/I/41UNF9Yv8+L.jpg" },
  { name: "Rice Vinegar", category: "Other", ingred_image_url: "https://shop.healthyoptions.com.ph/cdn/shop/products/070641064129_01.jpg?v=1630388517" },
  { name: "Sesame Oil", category: "Other", ingred_image_url: "https://cloudinary.images-iherb.com/image/upload/f_auto,q_auto:eco/images/edn/edn00025/y/28.jpg" },
  { name: "Maple Syrup", category: "Other", ingred_image_url: "https://www.pngplay.com/wp-content/uploads/8/Maple-Syrup-PNG-HD-Quality.png" },
  { name: "Egg", category: "Other", ingred_image_url: "https://png.pngtree.com/png-vector/20240521/ourmid/pngtree-fresh-single-egg-png-image_12504967.png" },
  { name: "Cheddar Cheese", category: "Other", ingred_image_url: "https://png.pngtree.com/png-clipart/20231003/original/pngtree-cheddar-cheese-wax-png-image_13244393.png" },
  { name: "Coconut Milk", category: "Other", ingred_image_url: "https://m.media-amazon.com/images/I/91wuVlZvFBL._SL1500_.jpg" }
];

const recipesData = [
  {
    name: "Simple Roasted Cauliflower",
    description: "A quick and easy dish featuring tender cauliflower florets roasted to golden perfection with a touch of olive oil and seasoning.",
    instructions: [
                        { number: 1, text: "Preheat oven to 450° F." },
                        { number: 2, text: "In a large bowl, toss cauliflower with rosemary, olive oil, salt, and pepper. Spread seasoned cauliflower on a large baking sheet (use two sheets if they are crowded on one)." },
                        { number: 3, text: "Roast for 15 minutes; remove from oven and stir." },
                        { number: 4, text: "Continue roasting for 10 minutes or until cauliflower is tender and lightly browned." },
                        { number: 5, text: "Taste and adjust seasoning as needed." }
                      ],
    calories: 90,
    cook_time: 25,
    servings: 9,
    image_url: "https://www.noracooks.com/wp-content/uploads/2025/04/roasted-cauliflower-3.jpg",
    category: "Lunch",
    nutri_info: ["2g carbs", "12g protein", "2.5g fat", "85mg sodium" ],
    ingredients: ["Black pepper", "Salt", "Olive Oil", "Fresh Rosemary", "Cauliflower"]
  },
  {
    name: "Chile Lime Shrimp with Poblano Sauce",
    description: "Juicy shrimp seasoned with chile and lime, pan-seared to perfection, and served with a creamy, smoky poblano sauce.",
    instructions: [
                        { number: 1, text: "Line a sheet pan with parchment paper and preheat the broiler on high. Broil the poblano pepper until slightly blackened, for about 5 minutes, turning a couple of times. Place in a bowl and cover with plastic wrap to steam for 5 minutes." },
                        { number: 2, text: "Once it’s cool enough to touch, peel off the skin. Remove the stem and slice the pepper open to remove the seeds and roughly chop the pepper." },
                        { number: 3, text: "Toss the shrimp with ½ tablespoon olive oil, 1 garlic clove, peppers, and pepper. Let it marinate for 15-30 minutes" },
                        { number: 4, text: "While the shrimp is marinating, finish the sauce. Blend the poblano pepper, Greek yogurt, 2 grated garlic cloves, cumin, kosher salt, and pepper in a food processor until smooth." },
                        { number: 5, text: "Heat the remaining ½ tablespoon of olive oil in a skillet. Once the pan is hot, sear the shrimp for 2 minutes on each side until just cooked through." },
                        { number: 6, text: "Serve with the poblano sauce." }
                      ],
    calories: 120,
    cook_time: 20,
    servings: 6,
    image_url: "https://tammycirceo.com/wp-content/uploads/2020/02/IMG_8829-W2.jpg",
    category: "Dinner",
    nutri_info: ["2g carbs", "12g protein", "3g fat", "470mg sodium" ],
    ingredients: ["Poblano Chile Pepper", "Garlic", "Plain Nonfat Greek yogurt", "Ground Cumin", "Kosher Salt", "Shrimp", "Serrano Pepper", "Black Pepper"]
  },
  {
    name: "Tofu and Vegetable Skewers",
    description: "Fire up the grill for a healthy dinner of Tofu and Vegetable Skewers. A delicious Asian-inspired marinade adds a burst of flavor to this diabetes-friendly meal.",
    instructions: [
                        { number: 1, text: "In a bowl, whisk together soy sauce, rice vinegar, sesame oil, maple syrup or agave nectar, minced garlic, and grated ginger to create the marinade." },
                        { number: 2, text: "Cut the pressed tofu into cubes and place them in a shallow dish. Pour half of the marinade over the tofu, ensuring each piece is coated. Let it marinate for at least 30 minutes." },
                        { number: 3, text: "While the tofu is marinating, prepare the vegetables. Thread the marinated tofu, red onion chunks, bell pepper chunks, and yellow squash rounds onto skewers, alternating between the ingredients." },
                        { number: 4, text: "Preheat the grill or grill pan over medium-high heat." },
                        { number: 5, text: "Grill the skewers for about 10–15 minutes, turning occasionally, until the tofu is golden and the vegetables are charred and tender. Baste the skewers with the remaining marinade during grilling for extra flavor." },
                        { number: 6, text: "Once cooked, remove the skewers from the grill and let them rest for a few minutes." }
                      ],
    calories: 90,
    cook_time: 15,
    servings: 4,
    image_url: "https://www.allrecipes.com/thmb/PMBRl1KXcnNym5ICMVC_3oWfDpQ=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/5153601-grilled-tofu-skewers-with-sriracha-sauce-Buckwheat-Queen-1x1-1-b18eec5e7e2342dfb772cd0d12666075.jpg",
    category: "Appetizer",
    nutri_info: ["21g carbs", "21g protein", "14g fat", "457mg sodium" ],
    ingredients: ["Soy sauce", "Rice vinegar", "Sesame oil", "Maple syrup", "Garlic", "Onion", "Ginger", "Bell Pepper", "Tofu", "Squash"]
  },
  {
    name: "Veggie Waffle Omelette",
    description: "This creative waffle omelette combines egg whites, colorful veggies, protein-rich breakfast that skips the carbs. ",
    instructions: [
                        { number: 1, text: "Set your waffle iron to a low or medium setting and allow it to preheat. Lightly coat both the top and bottom plates with cooking spray." },
                        { number: 2, text: "Place the egg whites in a large bowl. Whisk vigorously until slightly frothy." },
                        { number: 3, text: "Add the shredded cheese, diced turkey sausage, bell peppers, onions, and black pepper to the eggs. Stir until all ingredients are evenly distributed." },
                        { number: 4, text: "Pour a portion of the egg mixture onto the center of the preheated waffle iron, ensuring not to overfill. Close the lid gently. Cook for approximately 3-5 minutes, or until the eggs are fully set and the surface is lightly golden." },
                        { number: 5, text: "Carefully remove the omelette from the waffle iron using a spatula. Repeat the process with the remaining egg mixture. Serve warm, garnished with chives." }
                      ],
    calories: 150,
    cook_time: 15,
    servings: 1,
    image_url: "https://thecozycook.com/wp-content/uploads/2014/08/Waffle-Iron-Scrambled-Eggs-e1409265713828.jpg",
    category: "Breakfast",
    nutri_info: ["21g carbs", "21g protein", "14g fat", "457mg sodium" ],
    ingredients: ["Egg", "Cheddar Cheese", "Turkey Sausage", "Bell Pepper", "Onion", "Kosher Salt", "Black Pepper", "Chives"]
  },
  {
    name: "Spiced Ginger Carrot Soup",
    description: "With a touch of spice and a hint of warming ginger, it's the perfect balance of savory and sweet.",
    instructions: [
                        { number: 1, text: "In a large pot, heat the olive oil over medium heat. Add the chopped onion and sauté until translucent, 4–5 minutes." },
                        { number: 2, text: "Add the minced garlic and freshly grated ginger. Sauté for about 1–2 minutes until fragrant. Stir in the coriander and turmeric." },
                        { number: 3, text: "Add the chopped carrots to the pot and sauté for a few minutes, coating them with the aromatic spices." },
                        { number: 4, text: "Pour in the vegetable broth and bring the mixture to a gentle simmer. Cover the pot and let the carrots cook until they are tender." },
                        { number: 5, text: "Once the carrots are cooked, use an immersion blender to pureé the soup until smooth." },
                        { number: 6, text: "Stir in the coconut milk and season the soup with salt and pepper to taste. Serve the spiced ginger carrot soup hot, garnished with chopped fresh cilantro." }
                      ],
    calories: 90,
    cook_time: 15,
    servings: 8,
    image_url: "https://i5.walmartimages.com/dfw/7e496735-47a6/k2-_fff27782-f56e-4b91-bf4a-43bba6d5688c.v1.png",
    category: "Breakfast",
    nutri_info: ["10g carbs", "1g protein", "5g fat", "180mg sodium" ],
    ingredients: ["Carrot", "Garlic", "Onion", "Olive Oil", "Ginger", "Kosher Salt", "Coconut Milk", "Cilantro"]
  }
];

async function importData() {
  const ingredientMap = {};

  for (const item of ingredientsData) {
    const docRef = await db.collection('ingredients').add(item);
    ingredientMap[item.name] = docRef.id;
  }

  for (const recipe of recipesData) {
    const ingredientIds = recipe.ingredients
      .map(name => ingredientMap[name])
      .filter(id => !!id);

    const recipeDoc = {
      ...recipe,
      ingredients: ingredientIds
    };

    await db.collection('recipes').add(recipeDoc);
  }

  console.log("Ingredients and Recipes uploaded successfully.");
}

importData().catch(console.error);
