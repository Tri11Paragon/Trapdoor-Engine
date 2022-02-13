package com.trapdoor.engine.datatypes.ui;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.WindowSizeEvent;

/**
 * @author brett
 * @date Feb. 13, 2022
 * I went to type xplode but x sounded better
 */
public class XButton extends Button {
	
	private static final long serialVersionUID = 1649175322688137497L;

	private boolean centerVertical;
	private boolean centerHorizontal;
	private int x,y, width, height;
	private float scaling;
	
	public XButton(String text, boolean centeredHorizontal, boolean centeredVertical, float scaling, int x, int y, int width, int height) {
		super(text, x, y, width, height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.centerHorizontal = centeredHorizontal;
		this.centerVertical = centeredVertical;
		this.scaling = scaling;
		
		center((int) this.getParent().getSize().x, (int) this.getParent().getSize().y);
		
		super.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
			int lw = e.getWidth(), lh = e.getHeight();
			center(lw, lh);
		});
		
		super.getListenerMap().addListener(CursorEnterEvent.class, (e) -> {
			if (e.isEntered()) {
				super.setSize( (int) (this.width * 1.2), (int)(this.height * this.scaling) );
				center((int) this.getParent().getSize().x, (int) this.getParent().getSize().y);
			} else {
				super.setSize( (int) (this.width), (int)(height) );
				center((int) this.getParent().getSize().x, (int) this.getParent().getSize().y);
			}
		});
	}
	
	private void center(int sw, int sh) {
		if (centerHorizontal)
			super.setPosition(sw/2 + this.x - this.getSize().x/2, super.getPosition().y);
		if (centerVertical)
			super.setPosition(super.getPosition().x, sh/2 + this.y - this.getSize().y/2);
	}
	
}
