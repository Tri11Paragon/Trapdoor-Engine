package com.trapdoor.engine.renderer.ui.themes;

import imgui.ImGuiStyle;
import imgui.ImVec4;

import static imgui.flag.ImGuiCol.*;


import imgui.ImGui;

/**
 * @author laptop
 * @date Mar. 17, 2022
 * 
 */
public class AppleTheme implements ITheme {
	
	private boolean darkStyle;
	private float alpha;
	
	public AppleTheme(boolean darkStyle, float alpha) {
		this.darkStyle = darkStyle;
		this.alpha = alpha;
	}
	
	@Override
	public ImGuiStyle applyStyle(ImGuiStyle style) {
		
		style.setAlpha(1.0f);
        style.setFrameRounding(3.0f);
        style.setWindowRounding(6.0f);
        
        style.setColor(Text, 0.00f, 0.00f, 0.00f, 1.00f);
        style.setColor(TextDisabled, 0.60f, 0.60f, 0.60f, 1.00f);
        style.setColor(WindowBg, 0.94f, 0.94f, 0.94f, 0.94f);
        //style.setColor(ChildWindowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(PopupBg, 1.00f, 1.00f, 1.00f, 0.94f);
        style.setColor(Border, 0.00f, 0.00f, 0.00f, 0.39f);
        style.setColor(BorderShadow, 1.00f, 1.00f, 1.00f, 0.10f);
        style.setColor(FrameBg, 1.00f, 1.00f, 1.00f, 0.94f);
        style.setColor(FrameBgHovered, 0.26f, 0.59f, 0.98f, 0.40f);
        style.setColor(FrameBgActive, 0.26f, 0.59f, 0.98f, 0.67f);
        style.setColor(TitleBg, 0.96f, 0.96f, 0.96f, 1.00f);
        style.setColor(TitleBgCollapsed, 1.00f, 1.00f, 1.00f, 0.51f);
        style.setColor(TitleBgActive, 0.82f, 0.82f, 0.82f, 1.00f);
        style.setColor(MenuBarBg, 0.86f, 0.86f, 0.86f, 1.00f);
        style.setColor(ScrollbarBg, 0.98f, 0.98f, 0.98f, 0.53f);
        style.setColor(ScrollbarGrab, 0.69f, 0.69f, 0.69f, 1.00f);
        style.setColor(ScrollbarGrabHovered, 0.59f, 0.59f, 0.59f, 1.00f);
        style.setColor(ScrollbarGrabActive, 0.49f, 0.49f, 0.49f, 1.00f);
        //style.setColor(ComboBg, 0.86f, 0.86f, 0.86f, 0.99f);
        style.setColor(CheckMark, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(SliderGrab, 0.24f, 0.52f, 0.88f, 1.00f);
        style.setColor(SliderGrabActive, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(Button, 0.26f, 0.59f, 0.98f, 0.40f);
        style.setColor(ButtonHovered, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ButtonActive, 0.06f, 0.53f, 0.98f, 1.00f);
        style.setColor(Header, 0.26f, 0.59f, 0.98f, 0.31f);
        style.setColor(HeaderHovered, 0.26f, 0.59f, 0.98f, 0.80f);
        style.setColor(HeaderActive, 0.26f, 0.59f, 0.98f, 1.00f);
        //style.setColor(Column, 0.39f, 0.39f, 0.39f, 1.00f);
        //style.setColor(ColumnHovered, 0.26f, 0.59f, 0.98f, 0.78f);
        //style.setColor(ColumnActive, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ResizeGrip, 1.00f, 1.00f, 1.00f, 0.50f);
        style.setColor(ResizeGripHovered, 0.26f, 0.59f, 0.98f, 0.67f);
        style.setColor(ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
        //style.setColor(CloseButton, 0.59f, 0.59f, 0.59f, 0.50f);
        //style.setColor(CloseButtonHovered, 0.98f, 0.39f, 0.36f, 1.00f);
        //style.setColor(CloseButtonActive, 0.98f, 0.39f, 0.36f, 1.00f);
        style.setColor(PlotLines, 0.39f, 0.39f, 0.39f, 1.00f);
        style.setColor(PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        //style.setColor(ModalWindowDarkening, 0.20f, 0.20f, 0.20f, 0.35f);

        if( darkStyle ) {
            for (int i = 0; i <= COUNT; i++) {
                ImVec4 col = style.getColor(i);
                float H, S, V;
                float[] rgb = {col.x, col.y, col.z};
                float[] hsv = new float[3];
                ImGui.colorConvertRGBtoHSV( rgb, hsv );

                H = hsv[0];
                S = hsv[1];
                V = hsv[2];
                
                if( S < 0.1f ) {
                   V = 1.0f - V;
                }
                hsv[0] = H;
                hsv[1] = S;
                hsv[2] = V;
                
                ImGui.colorConvertHSVtoRGB( hsv, rgb );
                if( col.w < 1.00f ) {
                    col.w *= alpha;
                }
                
                col.x = rgb[0];
                col.y = rgb[1];
                col.z = rgb[2];
                style.setColor(i, col.x, col.y, col.z, col.w);
            }
        } else {
            for (int i = 0; i <= COUNT; i++) {
                ImVec4 col = style.getColor(i);
                if( col.w < 1.00f ) {
                    col.x *= alpha;
                    col.y *= alpha;
                    col.z *= alpha;
                    col.w *= alpha;
                }
            }
        }
		
		return style;
	}

}
