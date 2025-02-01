import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<RecipeModel>,
    private val onRecipeClick: (RecipeModel) -> Unit,
    private val onDeleteClick: (RecipeModel) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel) {
            binding.tvRecipeTitle.text = recipe.attributes.name
            binding.tvRecipeDescription.text = recipe.attributes.descriptions
            binding.btnDeleteRecipe.setOnClickListener { onDeleteClick(recipe) }
            binding.root.setOnClickListener { onRecipeClick(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    // ✅ Nueva función para actualizar los datos de la lista dinámicamente
    fun updateData(newRecipes: List<RecipeModel>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
