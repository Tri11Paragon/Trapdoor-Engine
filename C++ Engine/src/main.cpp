#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/utils.h"
#include "renderer/shader.h"
#include "renderer/camera.h"

static TD::debugUI* debugUIToolPtr;

static void keyCallBack(bool pressed, int code){
    if (code == GLFW_KEY_F3 && pressed)
        debugUIToolPtr->toggle();
    if (code == GLFW_KEY_ESCAPE && pressed)
        TD::setMouseGrabbed(!TD::isMouseGrabbed());
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
        1.0f, 1.0f,   // top right
        1.0f, 0.0f,   // bottom right
        0.0f, 0.0f,   // bottom left
        0.0f, 1.0f    // top left
};

int main(int, char**){
    init_logging("output");

    vector<font> fonts;
    fonts.push_back(font("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f));
    fonts.push_back(font("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f));

    fontContext fontContext(fonts);

    TD::window appWindow("GLFW Test", fontContext);
    TD::updateWindow(&appWindow);

    TD::IM_RegisterKeyListener(&keyCallBack);

    TD::firstPersonCamera camera;
    TD::debugUI debugUITool(&camera);
    debugUIToolPtr = &debugUITool;

    TD::shader triangleShader("../assets/shaders/triangle.vert", "../assets/shaders/triangle.frag");
    triangleShader.setUniformBlockLocation("Matrices", 1);
    TD::vao triangleVAO(vertices, texCoords, indices, 1);
    TD::texture benTexture("../assets/textures/ben.jpg");

    glm::mat4 trans = glm::mat4(1.0f);
    trans = glm::scale(trans, glm::vec3(0.5, 0.5, 0.5));

    triangleShader.use();
    triangleShader.setVec3("lightColor", glm::vec3(1.0));

    // Main loop
    while (!appWindow.isCloseRequested()) {
        appWindow.startRender(0.45f, 0.55f, 0.60f, 1.00f);
        camera.update();

        debugUITool.render(fontContext);

        triangleShader.use();
        trans = glm::rotate(trans, glm::radians(0.05f * 1000.0f / ImGui::GetIO().Framerate), glm::vec3(5.0, 0.5, 1.0));
        triangleShader.setMatrix("transform", trans);
        benTexture.enableGlTextures(1);
        benTexture.bind();
        triangleVAO.bind();
        //glEnableVertexAttribArray(0);
        //glDrawArrays(GL_TRIANGLES, 0, 3);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        triangleVAO.unbind();

        appWindow.finishRender();
    }

    return 0;
}
