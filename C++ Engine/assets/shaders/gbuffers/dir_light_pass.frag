#version 330 core
out vec4 FragColor;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

uniform vec3 direction;
uniform vec3 color;
uniform bool enabled;
uniform vec3 viewPos;
uniform vec2 screenSize;

vec2 CalcTexCoord() {
    return gl_FragCoord.xy / screenSize;
}

void main() {
    vec2 TexCoords = CalcTexCoord();
    vec4 posTexture = texture(gPosition, TexCoords);
    vec3 Diffuse = texture(gAlbedoSpec, TexCoords).rgb;
    if (!enabled){
        FragColor = vec4(0.0,0.0,0.0,0.0);
        if (posTexture.a >= 1.0)
            FragColor = vec4(Diffuse, 1.0);
        return;
    }
    // retrieve data from gbuffer
    vec3 FragPos = posTexture.rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    float Specular = texture(gAlbedoSpec, TexCoords).a;

    if (posTexture.a >= 1.0){
        FragColor = vec4(Diffuse, 1.0);
        return;
    }

    vec3 ambient = 0.1 * Diffuse;

    vec3 lightDir = normalize(direction);
    vec3 norm = normalize(Normal);
    float diff = max(dot(norm, lightDir), 0.0);

    vec3 diffuse = color * diff * Diffuse;

    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(norm, halfwayDir), 0.0), 16.0);
    vec3 specular = color * spec * Specular;

    FragColor = vec4(ambient + diffuse + specular, 1.0);
}