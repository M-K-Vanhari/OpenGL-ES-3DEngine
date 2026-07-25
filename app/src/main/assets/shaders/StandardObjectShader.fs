#version 320 es
precision highp float;

out vec4 fragColor;

in OUTSTRUCT{
    vec3 fragmentPosition;
    vec2 texCoord;
    vec3 tangentFragmentPosition;
    vec3 tangentPointLightPositions[1];
    vec3 tangentViewPosition;
    vec4 color;
} vIn;

uniform vec4 uColor;
uniform sampler2D uAlbedo;
uniform sampler2D uNormal;
uniform sampler2D uRoughness;

uniform vec3 uPointLightPositons[1];
uniform vec3 uPointLightColors[1];
uniform vec3 uAmbientLight;
uniform bool uTextured;
uniform bool uUseVertexColor;

float calculateBlinnPhong(vec3 tangentPointLightPosition, vec3 normal, float exponent){
    // Calculating diffuse lighting
    vec3 lightDirection = normalize(tangentPointLightPosition - vIn.tangentFragmentPosition);
    float diffuse =  max(dot(lightDirection, normal), 0.0);

    // Calculating specular lighting
    vec3 viewDirection = normalize(vIn.tangentViewPosition - vIn.tangentFragmentPosition);
    vec3 reflectDirection = reflect(-lightDirection, normal);
    vec3 halfwayDirection = normalize(viewDirection + lightDirection);

    float specularStrength = uTextured
        ? texture(uAlbedo, vIn.texCoord).r
        : 1.0;

    float specular = pow(max(dot(normal, halfwayDirection), 0.0), exponent)
                   * specularStrength;
    //float specular = pow(max(dot(normal, halfwayDirection), 0.0), exponent) * texture(uAlbedo, vIn.texCoord).r;

    return diffuse + specular;
}

void main(){
vec3 color;
vec3 normal;

if (uTextured) {
    color = texture(uAlbedo, vIn.texCoord).rgb;
    normal = texture(uNormal, vIn.texCoord).rgb;
    normal = normalize(normal * 2.0 - 1.0);
        vec4 blinnPhong = vec4(color * calculateBlinnPhong(vIn.tangentPointLightPositions[0], normal, 32.0) * uPointLightColors[0], 1.0);
        vec4 ambient = vec4(color * uAmbientLight, 1.0);
        fragColor = ambient + blinnPhong;
} else {
    color = uColor.rgb;
    normal = vec3(0.0, 0.0, 1.0);
    if (uUseVertexColor)
        fragColor = vIn.color;
    else
        fragColor = uColor;
}
    // Get values from textures
   // vec3 color = texture(uAlbedo, vIn.texCoord).rgb;
   // vec3 normal = texture(uNormal, vIn.texCoord).rgb;
    // Converting values to [-1, 1] range. Normal is already in tangent space
   // normal = normalize(normal * 2.0 - 1.0);



}
