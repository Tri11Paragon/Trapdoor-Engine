//
// Created by brett on 30/07/22.
//

#ifndef ENGINE_RENDERER_H
#define ENGINE_RENDERER_H

#include "shader.h"
#include "gl.h"
#include "../std.h"

namespace TD {

    class skyboxRenderer {
    private:
        TD::shader skyboxShader = TD::shader("../assets/shaders/skybox/skybox.vert",
                                             "../assets/shaders/skybox/skybox.frag");
        TD::cubemapTexture skyboxTexture = TD::cubemapTexture(std::vector<std::string>{
                "../assets/textures/skyboxes/basic_day/right.png",
                "../assets/textures/skyboxes/basic_day/left.png",
                "../assets/textures/skyboxes/basic_day/top.png",
                "../assets/textures/skyboxes/basic_day/bottom.png",
                "../assets/textures/skyboxes/basic_day/back.png",
                "../assets/textures/skyboxes/basic_day/front.png"
        });
        TD::vao skyboxVAO = TD::vao(TD::getCubeVertexPositions(250), TD::getCubeIndices(), 1);
    public:
        skyboxRenderer() {
            skyboxShader.use();
            skyboxShader.setColor("color1", 252.0, 187.0, 88.0);
            skyboxShader.setColor("color2", 88.0, 186.0, 252.0);
            skyboxShader.setBool("useColor", 0);
        }

        void render() {
            skyboxShader.use();
            skyboxTexture.bind();
            skyboxVAO.bind();
            skyboxVAO.draw();
        }
    };

    class renderer {
    private:

    public:

    };

}


#endif //ENGINE_RENDERER_H
