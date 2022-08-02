#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

struct Light {
    vec3 Position;
    vec3 Color;

    float Linear;
    float Quadratic;
    float Radius;
};
const int NR_LIGHTS = 50;
uniform Light lights[NR_LIGHTS];
uniform vec3 viewPos;

uniform vec3 direction;
uniform vec4 color;
uniform bool enabled;

vec3 calcDirLight(vec3 FragPos, vec3 Diffuse, vec3 Normal, float Specular){
    if (!enabled)
        return vec3(0);

    vec3 lightDir = normalize(direction);
    vec3 norm = normalize(Normal);
    float diff = max(dot(norm, lightDir), 0.0);

    vec3 diffuse = color.xyz * diff * Diffuse;

    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(norm, halfwayDir), 0.0), 16.0);
    vec3 specular = color.xyz * spec * Specular;

    return vec3(diffuse + specular);
}

vec3 CalcDirLight(vec3 FragPos, vec3 Diffuse, vec3 normal, float Specular) {
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 lightDir = normalize(direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16);
    // combine results
    vec3 diffuse  = color.xyz  * diff * Diffuse;
    vec3 specular = color.xyz * spec * vec3(Specular);
    return (diffuse + specular);
}

void main() {
    // retrieve data from gbuffer
    vec4 posTexture = texture(gPosition, TexCoords);
    vec3 FragPos = posTexture.rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    vec3 Diffuse = texture(gAlbedoSpec, TexCoords).rgb;
    float Specular = texture(gAlbedoSpec, TexCoords).a;

    if (posTexture.a >= 1.0){
        FragColor = vec4(Diffuse, 1.0);
        return;
    }

    // then calculate lighting as usual
    vec3 lighting = (Diffuse * 0.1) + calcDirLight(FragPos, Diffuse, Normal, Specular); // hard-coded ambient component
    vec3 viewDir = normalize(viewPos - FragPos);
    for(int i = 0; i < NR_LIGHTS; ++i) {
        // calculate distance between light source and current fragment
        float distance = length(lights[i].Position - FragPos);
        if(distance < lights[i].Radius) {
            // diffuse
            vec3 lightDir = normalize(lights[i].Position - FragPos);
            vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * lights[i].Color;
            // specular
            vec3 halfwayDir = normalize(lightDir + viewDir);
            float spec = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
            vec3 specular = lights[i].Color * spec * Specular;
            // attenuation
            float attenuation = 1.0 / (1.0 + lights[i].Linear * distance + lights[i].Quadratic * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;
            lighting += diffuse + specular;
        }
    }
    FragColor = vec4(lighting, 1.0);
}