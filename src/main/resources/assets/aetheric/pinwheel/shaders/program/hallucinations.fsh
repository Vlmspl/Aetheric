uniform sampler2D Sampler0;

in vec2 texCoord;

out vec4 FragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord);
    FragColor = color;
}