precision mediump float;
  uniform sampler2D samp;
  uniform sampler2D samp2;
  varying vec2 tc;
  varying float S;
void main(void) {
   vec4 t = texture2D(samp, tc);
   t.b += texture2D(samp2, tc).b;
   gl_FragColor = t;
}