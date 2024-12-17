package me.juancarloscp52.bedrockify.mixin.common.features.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.juancarloscp52.bedrockify.Bedrockify;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Set;

@Mixin(ServerRecipeManager.class)
public abstract class RecipeManagerMixin {

    @Unique
    private static Identifier bedrockify$getIdFromRecipeEntry(RecipeEntry<?> entry) {
        return entry.id().getValue();
    }

    @ModifyArg(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/recipe/PreparedRecipes;", at=@At(value = "INVOKE", target = "Lnet/minecraft/recipe/PreparedRecipes;of(Ljava/lang/Iterable;)Lnet/minecraft/recipe/PreparedRecipes;"), index = 0)
    public Iterable<RecipeEntry<?>> prepareRecipes(Iterable<RecipeEntry<?>> original){
        final List<RecipeEntry<?>> editable = Lists.newArrayList(original);
        final var editableIter = editable.iterator();

        final boolean bBERecipeEnabled = Bedrockify.getInstance().settings.isBedrockRecipesEnabled();

        // --- Procedure of Recipe modification.

        // namespace equals ${Bedrockify.MOD_ID}
        final Set<String> bedrockifyRecipeIds = Sets.newHashSet(
                editable.stream()
                        .filter(entry -> bedrockify$getIdFromRecipeEntry(entry).getNamespace().equals(Bedrockify.MOD_ID))
                        .map(entry -> bedrockify$getIdFromRecipeEntry(entry).getPath())
                        .toList()
        );

        // Identifier#path contains in both vanilla and bedrockify
        final Set<String> moddedRecipeIds = Sets.newHashSet(
                editable.stream()
                        .filter(entry -> {
                            final Identifier id = bedrockify$getIdFromRecipeEntry(entry);
                            return id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && bedrockifyRecipeIds.contains(id.getPath());
                        })
                        .map(entry -> bedrockify$getIdFromRecipeEntry(entry).getPath())
                        .toList()
        );

        // Process all the Recipes.
        while (editableIter.hasNext()) {
            var elem = editableIter.next();
            final Identifier recipeId = elem.id().getValue();
            final boolean bBERecipeIgnore = !bBERecipeEnabled && recipeId.getNamespace().equals(Bedrockify.MOD_ID);
            final boolean bConflictedVanillaRecipe = bBERecipeEnabled && moddedRecipeIds.contains(recipeId.getPath()) && recipeId.getNamespace().equals(Identifier.DEFAULT_NAMESPACE);

            if (bBERecipeIgnore || bConflictedVanillaRecipe) {
                editableIter.remove();
            }
        }

        return editable;
    }
}
