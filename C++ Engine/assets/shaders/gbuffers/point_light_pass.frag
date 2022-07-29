#version 330 core
out vec4 FragColor;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

in vec3 position_out;
in vec3 color_out;
in float linear_out;
in float quadratic_out;
in float radius_out;

uniform vec3 viewPos;
uniform vec2 screenSize;

vec2 CalcTexCoord() {
    return gl_FragCoord.xy / screenSize;
}

void main() {
    vec2 TexCoords = CalcTexCoord();
    // retrieve data from gbuffer
    vec4 posTexture = texture(gPosition, TexCoords);
    vec3 FragPos = posTexture.rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    vec3 Diffuse = texture(gAlbedoSpec, TexCoords).rgb;
    float Specular = texture(gAlbedoSpec, TexCoords).a;

    // no need to do lighting on far away objects ie the skybox
    if (posTexture.a >= 1.0){
        //FragColor = vec4(Diffuse, 1.0);
        return;
    }

    // then calculate lighting as usual
    vec3 lighting  = Diffuse * 0.1; // hard-coded ambient component
    vec3 viewDir  = normalize(viewPos - FragPos);
        // calculate distance between light source and current fragment
        vec3 posmfrag = position_out - FragPos;
        float distance = length(posmfrag);
        if(distance < radius_out) {
            // diffuse
            vec3 lightDir = normalize(posmfrag);
            vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * color_out;
            // specular
            vec3 halfwayDir = normalize(lightDir + viewDir);
            float spec = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
            vec3 specular = color_out * spec * Specular;
            // attenuation
            float attenuation = 1.0 / (1.0 + linear_out * distance + quadratic_out * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;
            lighting += diffuse + specular;
        }
    FragColor = vec4(lighting, 1.0);
}