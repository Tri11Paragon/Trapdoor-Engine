#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/utils.h"
#include "renderer/shader.h"

TD::debugUI debugUITool;

static void keyCallBack(bool pressed, int code){
    if (code == GLFW_KEY_F3 && pressed)
        debugUITool.toggle();
}

vector<float> vertices = {
        0.5f,  0.5f, 0.0f,  // top right
        0.5f, -0.5f, 0.0f,  // bottom right
        -0.5f, -0.5f, 0.0f,  // bottom left
        -0.5f,  0.5f, 0.0f   // top left
};

vector<unsigned int> indices = {
        0, 1, 3,   // first triangle
        1, 2, 3    // second triangle
};

vector<float> texCoords = {
        0.0f, 0.0f,  // lower-left corner
        1.0f, 0.0f,  // lower-right corner
        0.5f, 1.0f   // top-center corner
};

int main(int, char**){
    init_logging("output");

    vector<font> fonts;
    fonts.push_back(font("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f));
    fonts.push_back(font("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f));

    fontContext fontContext(fonts);

    window appWindow("GLFW Test", fontContext);

    TD::IM_RegisterKeyListener(&keyCallBack);

    TD::shader triangleShader("../assets/shaders/triangle.vert", "../assets/shaders/triangle.frag");
    TD::vao triangleVAO(vertices, indices, 1);

    // Main loop
    while (!appWindow.isCloseRequested()) {
        appWindow.startRender(0.45f, 0.55f, 0.60f, 1.00f);

        debugUITool.render(fontContext);

        triangleShader.use();
        triangleVAO.bind();
        //glEnableVertexAttribArray(0);
        //glDrawArrays(GL_TRIANGLES, 0, 3);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        triangleVAO.unbind();

        appWindow.finishRender();
    }

    return 0;
}
