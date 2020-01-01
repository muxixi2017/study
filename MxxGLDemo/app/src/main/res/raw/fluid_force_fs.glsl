precision mediump float;
  uniform sampler2D samp;
  uniform float c;
  varying vec2 tc;
  const float h = 1./512.;
void main(void) {
   vec4 t = texture2D(samp, tc);
   t.g += c*t.b;
   gl_FragColor = t;
}