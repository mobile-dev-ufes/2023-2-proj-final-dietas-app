package com.example.dietasapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dietasapp.data.Utils
import com.example.dietasapp.data.model.FoodModel
import com.example.dietasapp.data.model.MealModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.model.FieldIndex

/**
 * ViewModel for managing meal-related data.
 */
class MealsViewModel : ViewModel() {
    private var listMeals = MutableLiveData<MutableList<MealModel>>()
    private lateinit var dietId: String

    /**
     * Set the ID of the associated diet.
     * @param id The ID of the diet.
     */
    fun setDietId(id: String) {
        dietId = id
    }

    /**
     * Get LiveData for the list of meals.
     * @return LiveData containing the list of meals.
     */
    fun getListMeals(): LiveData<MutableList<MealModel>> {
        return listMeals
    }

    /**
     * Update the list of meals from the Firestore database.
     */
    fun updateAllMealsDB(){
        val colMealRef = Utils.Firestore.getUserMealsColRef(dietId)

        colMealRef.get().addOnSuccessListener {
            val list = mutableListOf<MealModel>()
            for (doc in it){
                val meal = doc.toObject(MealModel::class.java)
                meal.id = doc.id
                meal.dietId = dietId
                Log.i("MealsViewModelUpdate", "Meal: $meal")
                list.add(meal)
            }
            listMeals.value = list
        }
            .addOnFailureListener {
                Log.e("MealsViewModel", "Error fetching meals")
            }
    }

    /**
     * Create a new meal and add it to the Firestore database.
     * @param meal The meal to be created.
     */
    fun createMeals(meal: MealModel){
        val colMealRef = Utils.Firestore.getUserMealsColRef(dietId)
        colMealRef.add(meal)
            .addOnSuccessListener {
                Log.i("MealsViewModel", "Meal created successfully")
                updateAllMealsDB()
            }
            .addOnFailureListener {
                Log.e("MealsViewModel", "Error creating meal")
            }
    }

    /**
     * Update an existing meal in the Firestore database.
     * @param meal The updated meal.
     */
    fun updateMeals(meal: MealModel){
        Log.i("MealsViewModel", "Meal: $meal")
        val docMealRef = Utils.Firestore.getUserMealsDocRef(meal.dietId, meal.id)
        docMealRef.update("title", meal.title, "date", meal.date)
            .addOnSuccessListener {
                Log.i("MealsViewModel", "Meal updated successfully")
                updateAllMealsDB()
            }
            .addOnFailureListener {
                Log.e("MealsViewModel", "Error updating meal")
            }
    }

    /**
     * Delete a meal from the Firestore database.
     * @param m The meal to be deleted.
     */
    fun deleteMeal(m: MealModel) {
        val docMealRef = Utils.Firestore.getUserMealsDocRef(m.dietId, m.id)
        docMealRef.delete()
            .addOnSuccessListener {
                Log.i("MealsViewModel", "Meal deleted successfully")
                updateAllMealsDB()
            }
            .addOnFailureListener {
                Log.e("MealsViewModel", "Error deleting meal")
            }
    }

    /**
     * Updates the macronutrient values in a specific meal document in Firestore.
     *
     * This function increments the calorie, fat, protein, and carbohydrate values of a meal
     * identified by [mealId] based on the macronutrient values of the provided [food] model.
     *
     * @param mealId The ID of the meal document to be updated.
     * @param food The [FoodModel] containing macronutrient values to be added to the meal.
     */
    fun updatemacronutrients(mealId: String, food: FoodModel) {
        val docMealRef = Utils.Firestore.getUserMealsDocRef(dietId, mealId)
        docMealRef
            .update(
                "calorie", FieldValue.increment(food.calorie.toLong()),
                "fat", FieldValue.increment(food.fat.toLong()),
                "protein", FieldValue.increment(food.protein.toLong()),
                "carbohydrate", FieldValue.increment(food.carbohydrate.toLong())
            )
            .addOnSuccessListener {
                Log.i("MealsViewModel", "Macronutrients successfully updated")
            }
            .addOnFailureListener {
                Log.e("MealsViewModel", "Failed to update macronutrients")
            }
    }

    /**
     * Updates the macronutrient values in a specific meal document in Firestore based on the changes
     * between an old [FoodModel] and a new [FoodModel].
     *
     * This function calculates the difference in macronutrient values between the old and new foods
     * and then calls the [updatemacronutrients] function to update the corresponding meal document.
     *
     * @param mealId The ID of the meal document to be updated.
     * @param oldFood The original [FoodModel] with the old macronutrient values.
     * @param newFood The updated [FoodModel] with the new macronutrient values.
     */
    fun updatemacronutrients(mealId: String, oldFood: FoodModel, newFood: FoodModel) {
        val food = FoodModel(
            calorie = newFood.calorie - oldFood.calorie,
            fat = newFood.fat - oldFood.fat,
            protein = newFood.protein - oldFood.protein,
            carbohydrate = newFood.carbohydrate - oldFood.carbohydrate
        )
        updatemacronutrients(mealId, food)
    }

}