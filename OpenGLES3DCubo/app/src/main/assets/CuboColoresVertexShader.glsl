uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec4 a_color;
varying vec4 vColor;
void main() {
    vColor = a_color;
    gl_Position = uMVPMatrix * vPosition;
}