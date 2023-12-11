package com.example.dietasapp.UI.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.example.dietasapp.data.model.FoodUserModel
import com.example.dietasapp.databinding.FoodLineBinding

class ListFoodUserViewHolder(private val binding: FoodLineBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bindVH(food: FoodUserModel){
        binding.foodTitleText.text = food.food.title
        binding.foodDescriptionText.text = food.description
        binding.calText.text = food.food.calorie.toString()
        binding.carbText.text = food.food.carbohydrate.toString()
        binding.proteinText.text = food.food.protein.toString()
        binding.fatText.text = food.food.fat.toString()
    }

}