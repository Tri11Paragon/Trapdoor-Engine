#version 330 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D gRenderState;

const int NR_LIGHTS = 32;

layout (std140) uniform Lightings {
    // x y z, l
    vec4 Position[NR_LIGHTS];
    // q, r g b
    vec4 LightInfo[NR_LIGHTS];
};
uniform vec3 viewPos;
uniform vec3 directlight;
uniform float diffuseComponent;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
    vec4 renderState = texture(gRenderState, textureCoords);

    vec3 directlightDir = normalize(-directlight);
    vec3 FragPos = texture(gPosition, textureCoords).rgb;
    vec3 Normal = texture(gNormal, textureCoords).rgb;
    vec4 albedoSpec = texture(gAlbedoSpec, textureCoords);
    vec3 Diffuse = albedoSpec.xyz;
    float Specular = albedoSpec.w;
    
    if (renderState.x > 0.5f){
        // then calculate lighting as usual
        vec3 lighting  = Diffuse * max(diffuseComponent, 0.1); // hard-coded ambient component

        lighting += Diffuse * max(dot(Normal, directlightDir), 0.0);

        vec3 viewDir  = normalize(viewPos - FragPos);
        for(int i = 0; i < NR_LIGHTS; ++i) {
            // calculate distance between light source and current fragment
            vec3 lmp = Position[i].xyz - FragPos;
            float distance = length(lmp);
                // diffuse
            vec3 lightDir = normalize(lmp);
            vec3 lightColor = vec3(LightInfo[i].yzw);
            vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * lightColor;

            vec3 specular = vec3(0.0f);
            if (diffuse != 0.0f){
                    // specular (blinn phong)
                vec3 halfwayDir = normalize(lightDir + viewDir);  
                
                float specAmount = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
                
                specular = lightColor * specAmount * Specular;
            }
                // attenuation, 0.7f (l) 1.2f (q)
            float attenuation = 1.0 / (1.0 + Position[i].w * distance + LightInfo[i].x * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;

            lighting += (diffuse + specular);
        }    
        
        out_Color = vec4(lighting, 1.0);
    } else {
        out_Color = vec4(Diffuse, 1.0);
    }

}