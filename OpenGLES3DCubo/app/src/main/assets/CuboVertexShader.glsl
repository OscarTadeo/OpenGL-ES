attribute vec4 vPosition;
void main() {
    gl_Position = uMVPMatrix * vPosition;
}