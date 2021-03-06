package com.austinv11.thaumicnei.handlers;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.austinv11.thaumicnei.reference.Config;
import com.austinv11.thaumicnei.reference.Reference;
import com.austinv11.thaumicnei.utils.Logger;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InfusionHandler extends TemplateRecipeHandler {

	@Override
	public String getGuiTexture() {
		return "thaumcraft:textures/gui/gui_researchbook_overlay.png";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal(Reference.MOD_ID+":gui.nei.infusion");
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glScalef(2f, 2f, 2f);
		//GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(18, 13, 205, 75, 45, 45);//Infusion grid thingy

		GL11.glScalef(1f, 1f, 1f);
		GuiDraw.drawTexturedModalRect(34, -8, 21, 0, 15, 20);//Output slot
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void drawForeground(int recipe) {
		super.drawForeground(recipe);
	}

	private HashMap<String,int[]> getAspectCoords(AspectList aspects) {
		int[] startCoords = {0,400};
		int hBuffer = 300;//Space between two aspects side by side
		int vBuffer = 300;//Space between two aspects vertically
		int[] coords = {0,0};
		HashMap<String,int[]> map = new HashMap<String,int[]>();
		int aspectNum = aspects.getAspects().length;
		int i = 0;
		for (Aspect aspect : aspects.getAspects()) {
			if (aspectNum > 0) {
				if (i == 0) {
					if (coords[0] == 0 && coords[1] == 0) {
						coords = startCoords;
					}else {
						coords[0] = coords[0] - hBuffer;
						coords[1] = coords[1] + vBuffer;
					}
					i++;
				}else {
					if (coords[0] == 0 && coords[1] == 0) {
						coords = startCoords;
					}else {
						coords[0] = coords[0] + hBuffer;
					}
					i--;
				}
				map.put(aspect.getName(), coords.clone());
				aspectNum--;
			}else {
				break;
			}
		}
		return map;
	}

	private HashMap<String,int[]> getTextCoords(AspectList aspects) {
		int aspectNum = aspects.getAspects().length;
		int hBuffer = 20;//Space between two aspects side by side
		int vBuffer = 20;//Space between two aspects vertically
		int[] startCoords = {0,38};
		int[] coords = {0,0};
		int i = 0;
		HashMap<String,int[]> map = new HashMap<String,int[]>();
		for (Aspect aspect : aspects.getAspects()) {
			if (aspectNum > 0) {
				if (i == 0) {
					if (coords[0] == 0 && coords[1] == 0) {
						coords = startCoords;
					}else {
						coords[0] = coords[0] - hBuffer;
						coords[1] = coords[1] + vBuffer;
					}
					i++;
				}else {
					if (coords[0] == 0 && coords[1] == 0) {
						coords = startCoords;
					}else {
						coords[0] = coords[0] + hBuffer;
					}
					i--;
				}
				map.put(aspect.getName(), coords.clone());
				aspectNum--;
			}else {
				break;
			}
		}
		return map;
	}

	@Override
	public void drawExtras(int recipe) {
		CachedInfusionRecipe r = (CachedInfusionRecipe) arecipes.get(recipe);
		int instability = r.instability;
		GuiDraw.drawString(StatCollector.translateToLocal("tc.inst"),0,0,0x505050, false);
		//Logger.info(instability);
		GuiDraw.drawString(Instability.fromInt(instability).toString(),0,9,0xFFFFFF, false);
		HashMap<String,int[]> map = getAspectCoords(r.aspects);
		HashMap<String,int[]> textMap = getTextCoords(r.aspects);
		int coords[] = {0,0};
		int coords2[] = {0,0};
		GL11.glScalef(.065f,.065f,.065f);
		GL11.glEnable(GL11.GL_BLEND);
		for (Aspect aspect : r.aspects.getAspects()) {
			coords = map.get(aspect.getName());
			Color color = new Color(aspect.getColor());
			GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1f);
			GuiDraw.changeTexture(aspect.getImage());
			GuiDraw.drawTexturedModalRect(coords[0], coords[1], 0, 0, 260, 260);
		}
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
			if (recipes.get(i) instanceof InfusionRecipe) {
				InfusionRecipe recipe = (InfusionRecipe) recipes.get(i);
				if (ThaumcraftApiHelper.isResearchComplete(Reference.PLAYER_NAME, recipe.getResearch()) || Config.cheatMode){
					Object output = recipe.getRecipeOutput();
					if (output instanceof ItemStack) {
						if (((ItemStack)output).isItemEqual(result)) {
							if (checkDupe(recipe)) {
								this.arecipes.add(new CachedInfusionRecipe(recipe));
							}
						}
					}else {
						//TODO
					}
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		List recipes = ThaumcraftApi.getCraftingRecipes();
		for (int i = 0; i < recipes.size(); i++) {//Sorry, no enhanced for loop here again :P
			if (recipes.get(i) instanceof InfusionRecipe) {
				InfusionRecipe recipe = (InfusionRecipe) recipes.get(i);
				if (ThaumcraftApiHelper.isResearchComplete(Reference.PLAYER_NAME, recipe.getResearch()) || Config.cheatMode){
					if (recipe.getComponents() != null) {
						ArrayList<ItemStack> components = new ArrayList<ItemStack>(Arrays.asList(recipe.getComponents()));
						if (recipe.getRecipeInput().isItemEqual(ingredient) || components.contains(ingredient)) {
							if (checkDupe(recipe)) {
								this.arecipes.add(new CachedInfusionRecipe(recipe));
							}
						}
					}
				}
			}
		}
	}

	private boolean checkDupe(InfusionRecipe recipe) {
		for (Object o : this.arecipes.toArray()){
			if (o instanceof CachedInfusionRecipe){
				CachedInfusionRecipe r = (CachedInfusionRecipe) o;
				if (r.recipe.getRecipeInput() == recipe.getRecipeInput()){
					if (r.recipe.getRecipeOutput().equals(recipe.getRecipeOutput())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public class CachedInfusionRecipe extends CachedRecipe{
		private final int[] outCoords = {74,-3};
		private final int[] inCoords1 = {74,65};
		private final int[][] inCoords2 = {{74,25},{94,29},{109,44},{114,65},{109,86},{94,101},
				{74,105},{54,101},{39,86},{34,65},{39,44},{54,29}};//All the positions of items (clockwise)

		private PositionedStack output;
		private List<PositionedStack> inputs = new ArrayList<PositionedStack>();

		public AspectList aspects;
		public InfusionRecipe recipe;
		public int instability;

		public CachedInfusionRecipe(InfusionRecipe recipe){
			this.aspects = recipe.getAspects();
			this.output = new PositionedStack(recipe.getRecipeOutput(), outCoords[0], outCoords[1]);
			this.recipe = recipe;
			this.instability = recipe.getInstability();
			this.inputs.add(new PositionedStack(recipe.getRecipeInput(), inCoords1[0], inCoords1[1]));
			calcInputPositions(recipe.getComponents());
		}

		private void calcInputPositions(ItemStack[] items) {
			switch (items.length){
				case 1:
					this.inputs.add(new PositionedStack(items[0], inCoords2[6][0], inCoords2[6][1]));
					break;
				case 2:
					this.inputs.add(new PositionedStack(items[0], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[9][0], inCoords2[9][1]));
					break;
				case 3:
					this.inputs.add(new PositionedStack(items[0], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[9][0], inCoords2[9][1]));
					break;
				case 4:
					this.inputs.add(new PositionedStack(items[0], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[9][0], inCoords2[9][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[0][0], inCoords2[0][1]));
					break;
				case 5:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[11][0], inCoords2[11][1]));
					break;
				case 6:
					this.inputs.add(new PositionedStack(items[0], inCoords2[0][0], inCoords2[0][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[2][0], inCoords2[2][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[10][0], inCoords2[10][1]));
					break;
				case 7:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[5][0], inCoords2[5][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[7][0], inCoords2[7][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[9][0], inCoords2[9][1]));
					this.inputs.add(new PositionedStack(items[6], inCoords2[11][0], inCoords2[11][1]));
					break;
				case 8:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[2][0], inCoords2[2][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[5][0], inCoords2[5][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[7][0], inCoords2[7][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[6], inCoords2[10][0], inCoords2[10][1]));
					this.inputs.add(new PositionedStack(items[7], inCoords2[11][0], inCoords2[11][1]));
					break;
				case 9:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[2][0], inCoords2[2][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[5][0], inCoords2[5][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[7][0], inCoords2[7][1]));
					this.inputs.add(new PositionedStack(items[6], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[7], inCoords2[10][0], inCoords2[10][1]));
					this.inputs.add(new PositionedStack(items[8], inCoords2[11][0], inCoords2[11][1]));
					break;
				case 10:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[2][0], inCoords2[2][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[5][0], inCoords2[5][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[7][0], inCoords2[7][1]));
					this.inputs.add(new PositionedStack(items[6], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[7], inCoords2[9][0], inCoords2[9][1]));
					this.inputs.add(new PositionedStack(items[8], inCoords2[10][0], inCoords2[10][1]));
					this.inputs.add(new PositionedStack(items[9], inCoords2[11][0], inCoords2[11][1]));
					break;
				case 11:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[2][0], inCoords2[2][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[5][0], inCoords2[5][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[6][0], inCoords2[11][1]));
					this.inputs.add(new PositionedStack(items[6], inCoords2[7][0], inCoords2[7][1]));
					this.inputs.add(new PositionedStack(items[7], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[8], inCoords2[9][0], inCoords2[9][1]));
					this.inputs.add(new PositionedStack(items[9], inCoords2[10][0], inCoords2[10][1]));
					this.inputs.add(new PositionedStack(items[10], inCoords2[11][0], inCoords2[11][1]));
					break;
				case 12:
					this.inputs.add(new PositionedStack(items[0], inCoords2[1][0], inCoords2[1][1]));
					this.inputs.add(new PositionedStack(items[1], inCoords2[2][0], inCoords2[2][1]));
					this.inputs.add(new PositionedStack(items[2], inCoords2[3][0], inCoords2[3][1]));
					this.inputs.add(new PositionedStack(items[3], inCoords2[4][0], inCoords2[4][1]));
					this.inputs.add(new PositionedStack(items[4], inCoords2[5][0], inCoords2[5][1]));
					this.inputs.add(new PositionedStack(items[5], inCoords2[6][0], inCoords2[6][1]));
					this.inputs.add(new PositionedStack(items[6], inCoords2[7][0], inCoords2[7][1]));
					this.inputs.add(new PositionedStack(items[7], inCoords2[8][0], inCoords2[8][1]));
					this.inputs.add(new PositionedStack(items[8], inCoords2[9][0], inCoords2[9][1]));
					this.inputs.add(new PositionedStack(items[9], inCoords2[10][0], inCoords2[10][1]));
					this.inputs.add(new PositionedStack(items[10], inCoords2[11][0], inCoords2[11][1]));
					this.inputs.add(new PositionedStack(items[11], inCoords2[0][0], inCoords2[0][1]));
					break;
			}
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.inputs;
		}
	}

	private static enum Instability {
		NEGLIGIBLE(1),MINOR(3),MODERATE(4),HIGH(6),VERY_HIGH(8),DANGEROUS(13),ERROR(99);
		private int value;
		private Instability(int value) {
			this.value = value;
		}

		@Override
		public String toString(){
			switch(this.value){
				case 1:
					return StatCollector.translateToLocal("tc.inst.0");
				case 3:
					return StatCollector.translateToLocal("tc.inst.1");
				case 4:
					return StatCollector.translateToLocal("tc.inst.2");
				case 6:
					return StatCollector.translateToLocal("tc.inst.3");
				case 8:
					return StatCollector.translateToLocal("tc.inst.4");
				case 13:
					return StatCollector.translateToLocal("tc.inst.5");
				case 99:
					return StatCollector.translateToLocal(Reference.MOD_ID+":error");
			}
			return null;
		}

		public static Instability fromInt(int in){
			switch (in){
				case 0:
					return NEGLIGIBLE;
				case 1:
					return NEGLIGIBLE;
				case 2:
					return MINOR;
				case 3:
					return MINOR;
				case 4:
					return MODERATE;
				case 5:
					return MODERATE;
				case 6:
					return HIGH;
				case 7:
					return HIGH;
				case 8:
					return VERY_HIGH;
				case 9:
					return VERY_HIGH;
				default:
					//Logger.info(in);
					return DANGEROUS;
			}
		}
	}

	@Override
	public String getOverlayIdentifier(){
		return "infusion";
	}
}
