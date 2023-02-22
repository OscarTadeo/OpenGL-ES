uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec4 a_color;

attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;

varying vec4 vColor;
void main() {
    vColor = a_color;
    v_TexCoordinate = a_TexCoordinate;
    gl_Position = uMVPMatrix * vPosition;
}