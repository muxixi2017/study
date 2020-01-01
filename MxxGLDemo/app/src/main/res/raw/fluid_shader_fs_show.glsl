precision mediump float;
  uniform sampler2D samp;
  varying vec2 tc;
void main(void) {
   float T = texture2D(samp, tc).b;
   //T += texture2D(samp, vec2(1.0,1.0)-tc).b;
   gl_FragColor = vec4(T, 0., -T, 1.);
}