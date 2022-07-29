#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/ui.h"
#include "renderer/shader.h"
#include "renderer/camera.h"
#include "util.h"
#include "profiler.h"

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

    vector<TD::font> fonts;
    fonts.push_back(TD::font("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f));
    fonts.push_back(TD::font("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f));

    TD::fontContext::loadContexts(fonts);

    TD::window::initWindow("GLFW Test");

    TD::IM_RegisterKeyListener(&keyCallBack);

    TD::firstPersonCamera camera;
    TD::debugUI::changeActiveCamera(&camera);

    TD::shader skyboxShader("../assets/shaders/skybox/skybox.vert", "../assets/shaders/skybox/skybox.frag");
    skyboxShader.setFloat("useColor", 0);
    TD::cubemapTexture skyboxTexture(std::vector<std::string> {
        "../assets/textures/skyboxes/basic_day/right.png",
        "../assets/textures/skyboxes/basic_day/left.png",
        "../assets/textures/skyboxes/basic_day/top.png",
        "../assets/textures/skyboxes/basic_day/bottom.png",
        "../assets/textures/skyboxes/basic_day/back.png",
        "../assets/textures/skyboxes/basic_day/front.png"
    });
    TD::vao skyboxVAO(TD::getCubeVertexPositions(250), TD::getCubeIndices(), 1);

    TD::gBufferFBO gBufferFbo;

    TD::shader fxaaShader("../assets/shaders/postprocessing/filter-fxaa.vert", "../assets/shaders/postprocessing/filter-fxaa.frag");
    TD::fbo fxaaFBO(TD::DEPTH_BUFFER);
    fxaaFBO.createColorTexture(GL_COLOR_ATTACHMENT0);

    TD::model kent("../assets/models/kent.dae");

    std::vector<TD::Light> lights = {};

    // Standard Defered about 120fps @ 1024 lights (8.5ms)

    TD::profiler renderTimer("Render");
    int MAX_LIGHTS = 50;
    TD::random pos(-75, 75);
    TD::random color(0, 1);
    for (int i = 0; i < MAX_LIGHTS; i++){
        TD::Light light(
                pos.getVec3(),
                color.getVec3(),
                0.07,
                0.20,
                65
        );
        lights.push_back(light);
    }

    // Main loop
    while (!TD::window::isCloseRequested()) {
        TD::window::startRender(0.45f, 0.55f, 0.60f, 1.00f);
        camera.update();

        TD::debugUI::render();
        //ImGui::ShowDemoWindow();

        renderTimer.start("Geometry Pass");
        gBufferFbo.bindFirstPass();

        kent.draw(*gBufferFbo.getFirstPassShader(), std::vector<glm::vec3> {
            glm::vec3(0, 0, -1), glm::vec3(0, 0, -10), glm::vec3(12, 0, -1), glm::vec3(4, 21, -1), glm::vec3(6, -5, -1)
        });

        skyboxShader.use();
        skyboxTexture.bind();
        skyboxVAO.bind();
        skyboxVAO.draw();

        gBufferFbo.unbindFBO();
        renderTimer.end("Geometry Pass");

        //fxaaFBO.bindFBODraw();
        gBufferFbo.bindSecondPass();
        renderTimer.start("Point Lighting Pass");
        gBufferFbo.runPointLighting(camera, lights);
        renderTimer.end("Point Lighting Pass");
        renderTimer.start("Dir Lighting Pass");
        gBufferFbo.runDirLighting(camera, lights);
        renderTimer.end("Dir Lighting Pass");
        renderTimer.start("Finishing Pass");
        gBufferFbo.endSecondPass();
        //fxaaFBO.unbindFBO();

        //fxaaFBO.bindColorTexture(GL_TEXTURE0, GL_COLOR_ATTACHMENT0);
        //fxaaFBO.renderToQuad(fxaaShader);

        TD::window::finishRender();
        renderTimer.end("Finishing Pass");
    }

    TD::window::deleteWindow();

    return 0;
}
