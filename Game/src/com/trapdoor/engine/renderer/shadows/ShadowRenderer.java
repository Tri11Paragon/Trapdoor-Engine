package com.trapdoor.engine.renderer.shadows;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.UBOLoader;
import com.trapdoor.engine.camera.ICamera;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.WorldEntityStorage;

public class ShadowRenderer {
	
	private Matrix4f temp = new Matrix4f();
	private Matrix4f orthoProjection = new Matrix4f();
	private Matrix4f shadowView = new Matrix4f();
	private Matrix4f offset = new Matrix4f();
	
	private ShadowShader shader;
	private ShadowMap map;
	private ShadowBox box;
	
	public ShadowRenderer(ICamera camera) {
		shader = new ShadowShader();
		map = new ShadowMap();
		box = new ShadowBox(shadowView, camera);
	}
	
	public void renderDepthMap(ICamera camera, WorldEntityStorage storage) {
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, map.getDepthMapFBO());
		GL33.glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
		
		GL33.glClear(GL33.GL_DEPTH_BUFFER_BIT);
		
		box.update();
		
		// create the orthographic matrix
		shader.start();
		
		//updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
		updateOrthoProjectionMatrix(200, 200, 200);
		//orthoProjection.identity();
		//orthoProjection.ortho(-50, 50, -50, 50, 100, 100);
		updateLightViewMatrix(DisplayManager.lightDirection, box.getCenter());
		shadowView.translate((float) -camera.getPosition().x, (float) -camera.getPosition().y, (float) -camera.getPosition().z);
		//shadowView.identity();
		//shadowView.translate(box.getCenter());
		
		
		temp = orthoProjection.mul(shadowView);
		
		shader.loadPerspectiveMatrix(temp);
		UBOLoader.updateShadowMatrix(getToShadowMapSpaceMatrix());
		
		GL33.glDisable(GL33.GL_CULL_FACE);
		// render out the shadows
		storage.renderShadow(this);
		GL33.glEnable(GL33.GL_CULL_FACE);
		
		shader.stop();
		//GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * This biased projection-view matrix is used to convert fragments into
	 * "shadow map space" when rendering the main render pass. It converts a
	 * world space position into a 2D coordinate on the shadow map. This is
	 * needed for the second part of shadow mapping.
	 * 
	 * @return The to-shadow-map-space matrix.
	 */
	public Matrix4f getToShadowMapSpaceMatrix() {
		createOffset();
		return offset.mul(temp);
	}

	
	/**
	 * Updates the "view" matrix of the light. This creates a view matrix which
	 * will line up the direction of the "view cuboid" with the direction of the
	 * light. The light itself has no position, so the "view" matrix is centered
	 * at the center of the "view cuboid". The created view matrix determines
	 * where and how the "view cuboid" is positioned in the world. The size of
	 * the view cuboid, however, is determined by the projection matrix.
	 * 
	 * @param direction
	 *            - the light direction, and therefore the direction that the
	 *            "view cuboid" should be pointing.
	 * @param center
	 *            - the center of the "view cuboid" in world space.
	 */
	private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		direction.normalize();
		center.negate();
		shadowView.identity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		shadowView.rotate(pitch, Maths.rx);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		shadowView.rotate((float) -Math.toRadians(yaw), Maths.ry);
		//shadowView.translate(center);
	}

	/**
	 * Creates the orthographic projection matrix. This projection matrix
	 * basically sets the width, length and height of the "view cuboid", based
	 * on the values that were calculated in the {@link ShadowBox} class.
	 * 
	 * @param width
	 *            - shadow box width.
	 * @param height
	 *            - shadow box height.
	 * @param length
	 *            - shadow box length.
	 */
	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		orthoProjection.identity();
		orthoProjection.m00(2f / width);
		orthoProjection.m11(2f / height);
		orthoProjection.m22(-2f / length);
		orthoProjection.m33(1);
	}

	/**
	 * Create the offset for part of the conversion to shadow map space. This
	 * conversion is necessary to convert from one coordinate system to the
	 * coordinate system that we can use to sample to shadow map.
	 * 
	 * @return The offset as a matrix (so that it's easy to apply to other matrices).
	 */
	private Matrix4f createOffset() {
		offset.identity();
		offset.translate(0.5f, 0.5f, 0.5f);
		offset.scale(0.5f, 0.5f, 0.5f);
		return offset;
	}
	
	public ShadowShader getShader() {
		return shader;
	}
	
	public ShadowMap getShadowMap() {
		return map;
	}
	
	public void cleanup() {
		this.shader.cleanUp();
		this.map.cleanup();
	}
	
}
