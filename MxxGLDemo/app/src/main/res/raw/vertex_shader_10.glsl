uniform mat4 u_MVPMatrix;
attribute vec4 a_PositionCoord;
attribute vec2 a_TextureCoord;
varying  vec2 v_TextureCoord;
void main() {
    v_TextureCoord = a_TextureCoord;
    gl_Position = u_MVPMatrix * a_PositionCoord;
}
