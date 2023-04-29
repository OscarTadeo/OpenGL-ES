// Esta variable matriz proporciona el enlace para manipular las coordenadas.
uniform mat4 uMVPMatrix;

attribute vec4 vPosition;

void main() {

    // La matriz debe ser incluida como modificador de gl_Position.

    // Tenga en cuenta que el factor uMVPMatrix va al principio
    // para que el producto de multiplicación de matrices sea correcto.
    gl_Position = uMVPMatrix * vPosition;
}