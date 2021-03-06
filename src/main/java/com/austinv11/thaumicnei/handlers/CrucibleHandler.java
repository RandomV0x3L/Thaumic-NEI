package com.austinv11.thaumicnei.handlers;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.austinv11.thaumicnei.reference.Config;
import com.austinv11.thaumicnei.reference.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrucibleHandler extends TemplateRecipeHandler {

	@Override
	public String getGuiTexture() {
		return "thaumcraft:textures/blocks/crucible3.png";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal(Reference.MOD_ID+":gui.nei.crucible");
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glScalef(0.19f, 0.19f, 0.19f);
		//GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(290, 200, 11, 10, 235, 240);//Actual Crucible

		GuiDraw.changeTexture("thaumcraft:textures/gui/gui_research.png");
		GL11.glScalef(5.25f, 5.25f, 5.25f);
		GuiDraw.drawTexturedModalRect(35, 5, 0, 230, 24, 24);//Input Slot
		GuiDraw.drawTexturedModalRect(140, 46, 55, 230, 24, 24);//Output Slot

		GL11.glEnable(GL11.GL_BLEND);
		GuiDraw.changeTexture(Reference.MOD_ID+":textures/gui/crucible_arrow_1.png");
		GL11.glScalef(0.14f, 0.14f, 0.14f);
		//GL11.glRotatef(135f, 0f, 0f, 1f);
		GuiDraw.drawTexturedModalRect(730, 300, 0, 0, 250, 250);//Output Arrow

		GuiDraw.changeTexture(Reference.MOD_ID+":textures/gui/crucible_arrow_2.png");
		//GL11.glScalef(1f,1f,1f);
		GuiDraw.drawTexturedModalRect(425, 550, 0, 0, 260, 260);//Aspect Input Arrow

		GuiDraw.changeTexture(Reference.MOD_ID+":textures/gui/crucible_arrow_3.png");
		GuiDraw.drawTexturedModalRect(425, 8, 0, 0, 260, 260);//Item Input Arrow
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void drawForeground(int recipe) {
		super.drawForeground(recipe);
	}

	private HashMap<String,int[]> getAndDrawAspects(AspectList aspects) {
		int[] rows = {1325,1205,1535};//Y values are as follows: 1 row, 2 rows - row # 1, 2 rows - row #2
		int[] columns = {30,330,630};//X values are as follows: column #1, column #2, column #3
		int[] coords = {30,1205};
		int hBuffer = 300;//Space between two aspects side by side
		int vBuffer = 300;//Space between two aspects vertically
		HashMap<String,int[]> map = new HashMap<String,int[]>();
		int aspectNum = aspects.getAspects().length;
		int aspectCount = aspects.getAspects().length;
		int i = 0;
		int h = 0,k = 0;
		for (Aspect aspect : aspects.getAspects()) {
			if (aspectNum != 0) {
				if (aspectCount > 0 && aspectCount < 4) {
					coords[1] = rows[0];
					if (aspectNum == 1) {
						coords[0] = columns[1];
					}else if (aspectNum == 2) {
						if (i == 0) {
							coords[0] = columns[0];
						}else {
							coords[0] = columns[2];
						}
					}else if (aspectNum == 3) {
						coords[0] = columns[i];
					}
					Color color = new Color(aspect.getColor());
					GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1f);
					GuiDraw.changeTexture(aspect.getImage());
					GuiDraw.drawTexturedModalRect(coords[0], coords[1], 0, 0, 260, 260);
					i++;
					map.put(aspect.getName(),coords.clone());
				}else {
					int[] temp = {30,1205};
					temp[0] = coords[0] + (hBuffer*h);
					temp[1] = coords[1] + (vBuffer*k);
					Color color = new Color(aspect.getColor());
					GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1f);
					GuiDraw.changeTexture(aspect.getImage());
					GuiDraw.drawTexturedModalRect(temp[0], temp[1], 0, 0, 260, 260);
					if (h == 2) {
						k++;
						h = h - 2;
					}else {
						h++;
					}
				}
			}
		}
		return map;
	}

	private HashMap<String,int[]> getTextCoords(HashMap<String,int[]> map, AspectList aspects) {
		int aspectNum = aspects.getAspects().length;
		int hBuffer = 20;//Space between two aspects side by side
		int vBuffer = 20;//Space between two aspects vertically
		int i = 0;
		int[] startCoords = {11,90};
		HashMap<String,int[]> rMap = new HashMap<String,int[]>();
		int[] coords2 = {0,0};
		for (Aspect aspect : aspects.getAspects()) {
			int[] coords = map.get(aspect.getName());//DEPRECATED
			if (aspects.getAspects().length > 3) {
				if (aspectNum > 0) {
					if (i == 0) {
						if (coords2[0] == 0 && coords2[1] == 0) {
							coords2 = startCoords;
						} else {
							coords2[0] = coords2[0] - (hBuffer * 2);
							coords2[1] = coords2[1] + vBuffer;
						}
						i++;
					}else if (i == 1) {
						if (coords2[0] == 0 && coords2[1] == 0) {
							coords2 = startCoords;
						} else {
							coords2[0] = coords2[0] + hBuffer;
						}
						i++;
					}else {
						if (coords2[0] == 0 && coords2[1] == 0) {
							coords2 = startCoords;
						}else {
							coords2[0] = coords2[0] + hBuffer;
						}
						i = i - 2;
					}
					map.put(aspect.getName(), coords2.clone());
					aspectNum--;
				}else {
					break;
				}
			}else {
				switch (coords[0]){//FIXME DEPRECATED
					case 30:
						coords2[0] = 11;
						break;
					case 330:
						coords2[0] = 31;
						break;
					case 630:
						coords2[0] = 51;
						break;
				}
				coords2[1] = 102;
			}
			rMap.put(aspect.getName(),coords2.clone());
		}
		return rMap;
	}

	@Override
	public void drawExtras(int recipe) {
		CachedCrucibleRecipe r = (CachedCrucibleRecipe) arecipes.get(recipe);
		int coords2[] = {0,0};
		GL11.glScalef(.065f,.065f,.065f);
		GL11.glEnable(GL11.GL_BLEND);
		HashMap<String,int[]> map = getAndDrawAspects(r.aspects);
		HashMap<String,int[]> textMap = getTextCoords(map,r.aspects);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glScalef(15.625f,15.625f,15.625f);
		for (Aspect aspect : r.aspects.getAspects()){
			coords2 = textMap.get(aspect.getName());
			GuiDraw.drawString(r.aspects.getAmount(aspect)+"",coords2[0],coords2[1],0xFFFFFF, true);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		List recipes = ThaumcraftApi.getCraftingRecipes();
		for (int i = 0; i < recipes.size(); i++){//Sorry, no enhanced for loop here :P
			if (recipes.get(i) instanceof CrucibleRecipe) {
				CrucibleRecipe recipe = (CrucibleRecipe) recipes.get(i);
				if (ThaumcraftApiHelper.isResearchComplete(Reference.PLAYER_NAME, recipe.key) || Config.cheatMode){
					if (recipe.getRecipeOutput().isItemEqual(result)) {
						if (checkDupe(recipe)) {
							this.arecipes.add(new CachedCrucibleRecipe(recipe));
						}
					}
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		List recipes = ThaumcraftApi.getCraftingRecipes();
		for (int i = 0; i < recipes.size(); i++) {//Sorry, no enhanced for loop here again :P
			if (recipes.get(i) instanceof CrucibleRecipe) {
				CrucibleRecipe recipe = (CrucibleRecipe) recipes.get(i);
				if (ThaumcraftApiHelper.isResearchComplete(Reference.PLAYER_NAME, recipe.key) || Config.cheatMode){
					if (recipe.catalyst instanceof ItemStack) {
						ItemStack item = (ItemStack) recipe.catalyst;
						if (item.isItemEqual(ingredient)) {
							if (checkDupe(recipe)) {
								this.arecipes.add(new CachedCrucibleRecipe(recipe));
							}
						}
					}else {
						ArrayList<ItemStack> item = (ArrayList<ItemStack>) recipe.catalyst;
						if (item.contains(ingredient)) {
							if (checkDupe(recipe)) {
								this.arecipes.add(new CachedCrucibleRecipe(recipe));
							}
						}
					}
				}
			}
		}
	}

	private boolean checkDupe(CrucibleRecipe recipe) {
		for (Object o : this.arecipes.toArray()){
			if (o instanceof CachedCrucibleRecipe){
				CachedCrucibleRecipe r = (CachedCrucibleRecipe) o;
				if (r.recipe.catalyst == recipe.catalyst){
					if (r.recipe.getRecipeOutput().isItemEqual(recipe.getRecipeOutput())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public class CachedCrucibleRecipe extends CachedRecipe{
		private final int[] outCoords = {143,51};
		private final int[] inCoords = {40,10};

		private PositionedStack output;
		private PositionedStack inputs;

		public AspectList aspects;
		public CrucibleRecipe recipe;

		public CachedCrucibleRecipe(CrucibleRecipe recipe){
			this.aspects = recipe.aspects;
			this.output = new PositionedStack(recipe.getRecipeOutput(), outCoords[0], outCoords[1]);
			this.recipe = recipe;
			this.inputs = new PositionedStack(recipe.catalyst, inCoords[0], inCoords[1]);
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

		@Override
		public PositionedStack getIngredient() {
			return this.inputs;
		}
	}

	@Override
	public String getOverlayIdentifier(){
		return "crucible";
	}
}
