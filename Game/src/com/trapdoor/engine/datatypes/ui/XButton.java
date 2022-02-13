package com.trapdoor.engine.datatypes.ui;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.WindowSizeEvent;
import com.trapdoor.engine.display.DisplayManager;

/**
 * @author brett
 * @date Feb. 13, 2022 I went to type xplode but x sounded better
 */
public class XButton extends Button {

	private static final long serialVersionUID = 1649175322688137497L;

	private boolean centerVertical;
	private boolean centerHorizontal;
	private int x, y, width, height;
	private float scaling;
	private int sizeX, sizeY;

	public XButton(String text, boolean centeredHorizontal, boolean centeredVertical, float scaling, int x, int y,
			int width, int height) {
		super(text, x, y, width, height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.centerHorizontal = centeredHorizontal;
		this.centerVertical = centeredVertical;
		this.scaling = scaling;
		this.sizeX = (int) (this.width * this.scaling) - this.width;
		this.sizeY = (int) (this.height * this.scaling) - this.height;

		center((int) DisplayManager.WIDTH, (int) DisplayManager.HEIGHT);

		super.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
			int lw = e.getWidth(), lh = e.getHeight();
			center(lw, lh);
		});

		super.getListenerMap().addListener(CursorEnterEvent.class, (e) -> {
			if (e.isEntered()) {
				super.setSize((int) (this.width * this.scaling), (int) (this.height * this.scaling));
				centerEnter((int) this.getParent().getSize().x, (int) this.getParent().getSize().y);
			} else {
				super.setSize((int) (this.width), (int) (height));
				centerExit((int) this.getParent().getSize().x, (int) this.getParent().getSize().y);
			}
		});
	}

	private void center(int sw, int sh) {
		if (centerHorizontal)
			super.setPosition(sw / 2 + this.x - this.getSize().x / 2, super.getPosition().y);
		if (centerVertical)
			super.setPosition(super.getPosition().x, sh / 2 + this.y - this.getSize().y / 2);
	}
	
	private void centerEnter(int sw, int sh) {
		if (centerHorizontal)
			super.setPosition(sw / 2 + this.x - this.getSize().x / 2, super.getPosition().y);
		else
			super.setPosition(this.x - this.sizeX/2, super.getPosition().y);
		if (centerVertical)
			super.setPosition(super.getPosition().x, sh / 2 + this.y - this.getSize().y / 2);
		else
			super.setPosition(super.getPosition().x, this.y - this.sizeY/2);
	}
	
	private void centerExit(int sw, int sh) {
		if (centerHorizontal)
			super.setPosition(sw / 2 + this.x - this.getSize().x / 2, super.getPosition().y);
		else
			super.setPosition(this.x, super.getPosition().y);
		if (centerVertical)
			super.setPosition(super.getPosition().x, sh / 2 + this.y - this.getSize().y / 2);
		else
			super.setPosition(super.getPosition().x, this.y);
	}

}
