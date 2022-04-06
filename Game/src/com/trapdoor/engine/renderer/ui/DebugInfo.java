package com.trapdoor.engine.renderer.ui;

import java.text.DecimalFormat;

import org.lwjgl.glfw.GLFW;

import com.trapdoor.Main;
import com.trapdoor.engine.TextureLoader;
import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.renderer.functions.EntityRenderFunction;
import com.trapdoor.engine.tools.input.IKeyState;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.World;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

/**
 * @author laptop
 * @date Dec. 14, 2021
 * 
 */
public class DebugInfo implements IKeyState {
	
	public static float x,y,z;
	public static ImBoolean enableLines = new ImBoolean();
	private static DebugInfo th;
	
	private String gameVersion = "";
	
	private String mainRenderings = "";
	private String mainThreadFPS = "";
	private String entityCount = "";
	private String entityRenderCount = "";
	private String renderCount = "";
	
	private String physics = "";
	private String physicsThreadFPS = "";
	
	private String playerPosition = "";
	
	private String javaMemory = "";
	private String javaMemoryNon = "";
	
	private String particleCount = "";
	
	private boolean enabled = false;
	private boolean frametimes = false;
	private StringBuilder builder;
	private World world;
	private DataAnalysis fpsData;
	
	public DebugInfo() {
		th = this;
		builder = new StringBuilder();
		
		gameVersion = DisplayManager.gameName + " " + DisplayManager.gameVersion + " // " + DisplayManager.engineName + " " + DisplayManager.engineVersion;
		mainRenderings = "Renderer:";
		physics = "Physics:";
		fpsData = new DataAnalysis("Frametimes", 64512);
		fpsData.setAutoClear(64000);
	}
	
	private final DecimalFormat df = new DecimalFormat("#.###");
	private String round(double d) {
		return df.format(d);
	}
	
	private void includeSpace(StringBuilder sb, String x) {
		sb.append(x);
	}
	
	/**
	 * called every second
	 */
	public void updateInfo() {
		if (!enabled)
			return;
		builder = new StringBuilder();
		builder.append("FPS  ");
		builder.append((int)DisplayManager.getFPS());
		builder.append("  Frametime (ms):  ");
		builder.append(round(DisplayManager.getFrameTimeMilis()));
		mainThreadFPS = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("VAO:  ");
		builder.append(VAOLoader.getVAOS());
		builder.append("  VBO:  ");
		builder.append(VAOLoader.getVBOS());
		builder.append("  Textures:  ");
		builder.append(TextureLoader.getTextures());
		renderCount = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("UPS  ");
		builder.append((int)Threading.getFPS());
		builder.append("  Updatetime (ms):  ");
		builder.append(round(Threading.getFrameTimeMilis()));
		physicsThreadFPS = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("Heap: ");
		builder.append(Main.mx.getHeapMemoryUsage().getUsed() / 1024 / 1024);
		builder.append("mb / ");
		builder.append(Main.mx.getHeapMemoryUsage().getCommitted() / 1024 / 1024);
		builder.append("mb [");
		builder.append(Main.mx.getHeapMemoryUsage().getMax() / 1024 / 1024);
		builder.append("mb]");
		javaMemory = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("Non-Heap: ");
		builder.append(Main.mx.getNonHeapMemoryUsage().getUsed() / 1024 / 1024); 
		builder.append("mb / ");
		builder.append(Main.mx.getNonHeapMemoryUsage().getCommitted() / 1024 / 1024);
		builder.append("mb [");
		builder.append(Main.mx.getNonHeapMemoryUsage().getMax() / 1024 / 1024);
		builder.append("mb]");
		javaMemoryNon = (builder.toString());
		
	}
	
	/**
	 * called every frame
	 */
	public void updateFrame() {
		fpsData.add(DisplayManager.getFrameTimeMilisR());
		OptionsMenu.menu.set(enabled);
		if (!enabled)
			return;
		
		builder = new StringBuilder();
		builder.append("X:  ");
		includeSpace(builder, round(x));
		builder.append(" | Y: ");
		includeSpace(builder, round(y));
		builder.append(" | Z: ");
		includeSpace(builder, round(z));
		playerPosition = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("Particle Count: ");
		builder.append(world.getParticleCount()); 
		builder.append(" : ");
		builder.append(world.getParticleSize()); 
		particleCount = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("Entities: ");
		builder.append(World.entityCount);
		entityCount = (builder.toString());
		
		builder = new StringBuilder();
		builder.append("Rendered Ents: ");
		builder.append(EntityRenderFunction.getCount());
		entityRenderCount = builder.toString();
		
		ImGui.pushFont(GameRegistry.getFont("roboto-regular"));
		
		ImGui.setNextWindowPos(DisplayManager.windowPosX + 5, DisplayManager.windowPosY + 5, ImGuiCond.Appearing);
		ImGui.begin("Debug", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse);
		
		ImGui.text(gameVersion);
		
		ImGui.newLine();
			ImGui.text(mainRenderings);
			ImGui.bullet();
			ImGui.text(mainThreadFPS);
			ImGui.bullet();
			ImGui.text(renderCount);
		
		ImGui.newLine();
			ImGui.text(physics);
			ImGui.bullet();
			ImGui.text(physicsThreadFPS);
			ImGui.bullet();
			ImGui.text(entityCount);
			ImGui.bullet();
			ImGui.text(entityRenderCount);
			ImGui.bullet();
			ImGui.text(particleCount);
		
		ImGui.newLine();
			ImGui.text(playerPosition);
		
		ImGui.newLine();
			ImGui.text(javaMemory);
			ImGui.text(javaMemoryNon);
		
		ImGui.newLine();
		ImGui.beginChild("Buttons", 260, 100);
			ImGui.checkbox("Draw Lines?", enableLines);
			if (ImGui.button("Force GC", 80, 40)) {
				System.gc();
			}
		ImGui.endChild();
		
		ImGui.end();
		
		ImGui.popFont();
		if (frametimes)
			fpsData.drawWindow();
	}
	
	/**
	 * called every 250ms
	 */
	public void update() {
		if (!enabled)
			return;
		
	}
	
	private boolean allow = true;
	
	@Override
	public void onKeyPressed(int keys) {
		if (keys == GLFW.GLFW_KEY_F && enabled && Keyboard.isKeyDown(GLFW.GLFW_KEY_F3)) {
			frametimes = !frametimes;
			allow = false;
		}
	}

	@Override
	public void onKeyReleased(int keys) {
		if (keys == GLFW.GLFW_KEY_F3 && allow) {
			AnnotationHandler.cleanScreen();
			enabled = !enabled;
		} 
		if (keys == GLFW.GLFW_KEY_F3 && !allow)
			allow = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public static void assignWorld(World world) {
		th.world = world;
	}
	
}
