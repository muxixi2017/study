precision mediump float;

varying mediump vec2 v_TextureCoord;
uniform sampler2D u_Texture;

void main() {
    vec4 texture = texture2D(u_Texture, v_TextureCoord);
    float luminance = 0.299*texture.r+0.587*texture.g+0.114*texture.b;
    gl_FragColor = vec4(luminance,luminance,luminance,1);
}