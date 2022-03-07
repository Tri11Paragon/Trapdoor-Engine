#version 330 core

in vec2 textureCoords;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 bright_Color;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D gRenderState;
uniform sampler2D ssaoColor;

const int NR_LIGHTS = 32;

layout (std140) uniform Lightings {
    // x y z, l
    vec4 Position[NR_LIGHTS];
    // q, r g b
    vec4 LightInfo[NR_LIGHTS];
};
uniform vec3 viewPos;

uniform vec3 directLight;
uniform vec3 directLightColor;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

//const float gamma = 2.2;

void main(){
    vec4 renderState = texture(gRenderState, textureCoords);

    vec3 directlightDir = normalize(directLight);
    vec3 FragPos = texture(gPosition, textureCoords).rgb;
    vec3 Normal = texture(gNormal, textureCoords).rgb;
    vec4 albedoSpec = texture(gAlbedoSpec, textureCoords);
    vec3 Diffuse = albedoSpec.xyz;
    // TODO: impliment this
    float specularMapAmount = albedoSpec.w;
    
    if (renderState.x > 0.0f){
        if (renderState.x > 1.0f){
            out_Color = albedoSpec;
        } else {
            float AmbientOcclusion = texture(ssaoColor, textureCoords).r;
            Diffuse = Diffuse * renderState.b;
            // then calculate lighting as usual
            vec3 lighting = Diffuse * 0.03f * (AmbientOcclusion); // hard-coded ambient component

            // add directional lighting
            lighting += Diffuse * (max(dot(Normal, directlightDir), 0.0) * directLightColor);

            vec3 viewDir  = normalize(-FragPos);

            // Normal Lighting
            for(int i = 0; i < NR_LIGHTS; ++i) {
                // calculate distance between light source and current fragment
                vec3 lmp = Position[i].xyz - FragPos;
                float distance = length(lmp);
                    // diffuse
                vec3 lightDir = normalize(lmp);
                vec3 lightColor = vec3(LightInfo[i].yzw);
                vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * lightColor;

                vec3 specular = vec3(0.0f);
                if (diffuse != vec3(0.0f)){
                        // specular (blinn phong)
                    vec3 halfwayDir = normalize(lightDir + viewDir);  
                    
                    float specAmount = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
                    
                    specular = lightColor * specAmount * specularMapAmount;
                }
                    // attenuation, 0.7f (l) 1.2f (q)
                float attenuation = 1.0 / (1.0 + Position[i].w * distance + LightInfo[i].x * distance * distance);
                diffuse *= attenuation;
                specular *= attenuation;

                lighting += (diffuse * AmbientOcclusion + specular);
            }    
            
            out_Color = vec4(lighting, 1.0);
        }
        // apply gamma correction
        //out_Color.rgb = pow(out_Color.rgb, vec3(1.0/gamma));

        float brightness = dot(out_Color.rgb, vec3(0.2126, 0.7152, 0.0722));
        bright_Color = vec4(out_Color.rgb, 1.0) * brightness * brightness;
    } else {
        out_Color = vec4(Diffuse, 1.0);
        bright_Color = vec4(0.0, 0.0, 0.0, 1.0);
    }

}