const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Step 1: Ingredient list (from previous data, 20 ingredients)
const ingredientsData = [
  // Meat
  { name: "Chicken", category: "Meat", ingred_image_url: "https://media.istockphoto.com/id/1282866808/photo/fresh-raw-chicken.jpg?s=612x612&w=0&k=20&c=QtfdAhdeIGpR3JUNDmYFo6cN0el8oYMcOXMQI7Qder4=" },
  { name: "Pork", category: "Meat", ingred_image_url: "https://www.mercatogourmet.com.hk/cdn/shop/products/Australian_Free_Range_Pork_Loin_-_Rind_Off.jpg?v=1582787846&width=1500" },
  { name: "Shrimp", category: "Meat", ingred_image_url: "https://www.markwellfoods.com.au/wp-content/uploads/2022/03/raw-prawn-tail-free-scaled.jpg" },
  { name: "Oxtail", category: "Meat", ingred_image_url: "https://rarefoodshop.com/cdn/shop/files/ox-tail-500g-36266121625780.png?v=1743572564" },
  { name: "Tripes", category: "Meat", ingred_image_url: "https://consistent.com.ph/wp-content/uploads/2022/09/tripe2.jpg" },

  // Vegetables
  { name: "Garlic", category: "Vegetable", ingred_image_url: "https://www.veggycation.com.au/siteassets/veggycationvegetable/garlic.jpg" },
  { name: "Onion", category: "Vegetable", ingred_image_url: "https://bf1af2.a-cdn.akinoncloud.com/products/2024/10/01/151188/40731652-6590-4443-a875-7278a56188b0_size3840_cropCenter.jpg" },
  { name: "Carrot", category: "Vegetable", ingred_image_url: "https://www.themeatbox.co.nz/cdn/shop/files/TMBProductImages_23_600x.png?v=1705350837" },
  { name: "Eggplant", category: "Vegetable", ingred_image_url: "https://www.veggycation.com.au/siteassets/veggycationvegetable/eggplant.jpg" },
  { name: "String beans", category: "Vegetable", ingred_image_url: "https://safeselect.ph/cdn/shop/products/StringBeans.jpg?v=1641874408" },

  // Fruits
  { name: "Tomato", category: "Fruit", ingred_image_url: "https://veggies.my/cdn/shop/products/Tomatoes.png?v=1653972536" },
  { name: "Banana heart", category: "Fruit", ingred_image_url: "https://www.organics.ph/cdn/shop/products/banana-heart-500grams-fruits-vegetables-fresh-produce-673273_800x.jpg?v=1613208716" },
  { name: "Calamansi", category: "Fruit", ingred_image_url: "https://dizonfarms.net/wp-content/uploads/2023/09/Calamansi-3-1.jpg" },
  { name: "Radish", category: "Fruit", ingred_image_url: "https://safeselect.ph/cdn/shop/products/KoreanRadish_345x@2x.jpg?v=1641873903" },
  { name: "Kangkong", category: "Fruit", ingred_image_url: "https://4rfreshandfrozen.com/cdn/shop/products/kangkong_2048x2048.jpg?v=1586781186" },

  // Other
  { name: "Soy sauce", category: "Other", ingred_image_url: "https://assets.shop.loblaws.ca/products/20139275/b2/en/front/20139275_front_a06_@2.png" },
  { name: "Vinegar", category: "Other", ingred_image_url: "https://ever.ph/cdn/shop/files/100000060698-Heinz-Distilled-White-Vinegar-16oz-230629_5e8c90e5-bdb7-45b1-9161-adb950cf79f8.jpg?v=1725513164" },
  { name: "Fish sauce", category: "Other", ingred_image_url: "https://cloudinary.images-iherb.com/image/upload/f_auto,q_auto:eco/images/tkt/tkt00100/l/8.jpg" },
  { name: "Cooking oil", category: "Other", ingred_image_url: "https://imartgrocersph.com/wp-content/uploads/2020/09/Simply-Canola-Oil-1L.jpg" },
  { name: "Peanut butter", category: "Other", ingred_image_url: "https://www.sante.com.pl/wp-content/uploads/2021/09/maslo-orzechowe-350g-smooth-klasyczne-Sante.jpg" }
];

// Step 2: Recipes with ingredient names
const recipesData = [
  {
    name: "Chicken Adobo",
    description: "A savory Filipino classic made with chicken simmered in soy sauce, vinegar, garlic, and bay leaves.",
    instructions: [
                        { number: 1, text: "In a large bowl, combine chicken, soy sauce, and garlic. Let it marinate for at least 30 minutes." },
                        { number: 2, text: "Heat oil in a pan over medium heat. Sauté garlic until golden, then add the marinated chicken (reserve the marinade)." },
                        { number: 3, text: "Cook the chicken until lightly browned on all sides." },
                        { number: 4, text: "Add the reserved marinade, vinegar, bay leaves, and peppercorns. Bring to a boil without stirring." },
                        { number: 5, text: "Lower heat, cover, and simmer for 30–40 minutes or until chicken is tender and sauce is reduced." },
                        { number: 6, text: "Taste and adjust seasoning as needed. Serve hot with steamed rice." }
                      ],
    cook_time: 45,
    servings: 4,
    image_url: "https://www.recipetineats.com/tachyon/2015/02/Filipino-Chicken-Adobo_6.jpg?resize=900%2C1260&zoom=0.72",
    category: "Lunch",
    ingredients: ["Chicken", "Soy sauce", "Vinegar", "Garlic", "Bay leaves", "Black peppercorns", "Cooking oil"]
  },
  {
    name: "Sinigang na Baboy",
    description: "A hearty and tangy pork soup with tamarind, vegetables, and a savory broth.",
    instructions: [
                        { number: 1, text: "Boil pork in water, and skim off any scum that forms on the surface." },
                        { number: 2, text: "Add onions, tomatoes, and radish, and cook for 20 minutes." },
                        { number: 3, text: "Add the sinigang mix or fresh tamarind, then season with fish sauce and salt to taste." },
                        { number: 4, text: "Add the remaining vegetables (eggplant, okra, kangkong), and cook until tender." },
                        { number: 5, text: "Taste and adjust the sourness and seasoning as needed. Serve hot with rice." }
                      ],
    cook_time: 60,
    servings: 6,
    image_url: "https://panlasangpinoy.com/wp-content/uploads/2022/09/sinigang-na-baboy-730x1095.jpg",
    category: "Dinner",
    ingredients: ["Pork", "Tamarind", "Onion", "Tomato", "Radish", "Eggplant", "Okra", "Kangkong", "Fish sauce"]
  },
  {
    name: "Pancit Canton",
    description: "A popular Filipino stir-fried noodle dish with vegetables, meat, and seafood.",
    instructions: [
                        { number: 1, text: "Sauté garlic, onion, and pork until lightly browned." },
                        { number: 2, text: "Add shrimp, chicken, and vegetables. Stir-fry for 5 minutes." },
                        { number: 3, text: "Add soy sauce and chicken stock, then bring to a simmer." },
                        { number: 4, text: "Add Pancit Canton noodles, tossing until noodles are evenly coated with the sauce." },
                        { number: 5, text: "Cook for another 5–7 minutes until noodles are tender. Serve hot, garnished with calamansi (optional)." }
                      ],
    cook_time: 30,
    servings: 4,
    image_url: "https://www.maggi.ph/sites/default/files/styles/home_stage_944_531/public/srh_recipes/5b661360b8e49f5c2348c06858bb8f57.jpg?h=4f5b30f1&itok=doXJkNdF",
    category: "Appetizer",
    ingredients: ["Pancit Canton noodles", "Shrimp", "Chicken", "Pork", "Garlic", "Onion", "Carrot", "String beans", "Soy sauce", "Chicken stock"]
  },
  {
    name: "Kare-Kare",
    description: "A rich Filipino stew with oxtail, tripes, and a peanut-based sauce.",
    instructions: [
                        { number: 1, text: "Boil oxtail and tripes in water for about 2–3 hours until tender." },
                        { number: 2, text: "In a separate pan, sauté garlic and onion until fragrant, then add the peanut butter and rice flour to make the sauce." },
                        { number: 3, text: "Add the boiled oxtail and tripes into the sauce, then stir in the vegetables (eggplant, banana hearts, and string beans)." },
                        { number: 4, text: "Simmer until vegetables are tender. Serve hot with a side of bagoong." }
                      ],
    cook_time: 180,
    servings: 6,
    image_url: "https://www.kuserrano.com/wp-content/uploads/2023/04/bagnet-kare-kare.jpg",
    category: "Lunch",
    ingredients: ["Oxtail", "Tripes", "Peanut butter", "Rice flour", "Eggplant", "Banana heart", "String beans", "Garlic", "Onion", "Bagoong"]
  },
  {
    name: "Lumpiang Shanghai",
    description: "Crispy Filipino spring rolls filled with seasoned ground pork and vegetables.",
    instructions: [
                        { number: 1, text: "In a bowl, combine ground pork, minced garlic, onion, and carrot. Season with salt and pepper." },
                        { number: 2, text: "Place a spoonful of the mixture in a lumpia wrapper, roll tightly, and seal the edges with a bit of water." },
                        { number: 3, text: "Heat oil in a pan, and fry the lumpia until golden brown and crispy." },
                        { number: 4, text: "Drain excess oil on paper towels, and serve with sweet and sour sauce." }
                      ],
    cook_time: 30,
    servings: 4,
    image_url: "https://assets.unileversolutions.com/recipes-v2/166515.jpg",
    category: "Filipino",
    ingredients: ["Ground pork", "Garlic", "Onion", "Carrot", "Lumpia wrappers", "Oil", "Salt and pepper", "Sweet and sour sauce"]
  }
];

async function importData() {
  const ingredientMap = {}; // name -> Firestore ID

  // Step 1: Upload Ingredients
  for (const item of ingredientsData) {
    const docRef = await db.collection('ingredients').add(item);
    ingredientMap[item.name] = docRef.id;
  }

  // Step 2: Upload Recipes with Ingredient IDs
  for (const recipe of recipesData) {
    const ingredientIds = recipe.ingredients
      .map(name => ingredientMap[name])
      .filter(id => !!id); // filter out undefined ones

    const recipeDoc = {
      ...recipe,
      ingredients: ingredientIds
    };

    await db.collection('recipes').add(recipeDoc);
  }

  console.log("✅ Ingredients and Recipes uploaded successfully.");
}

importData().catch(console.error);
