precision mediump float;
  uniform sampler2D samp;
  varying vec2 tc;
  const float h = 1./512.;
void main(void) {
   vec4 t = texture2D(samp, tc);
   vec4 left = texture2D(samp, vec2(tc.r - h, tc.g));
   vec4 right = texture2D(samp, vec2(tc.r+h, tc.g));
   vec4 up = texture2D(samp, vec2(tc.r, tc.g + h));
   vec4 down = texture2D(samp, vec2(tc.r, tc.g - h));
   float uxX = (right.r-left.r)/2.;
   float uyY = (up.g-down.g)/2.;
   t.a = (left.a+right.a+up.a+down.a+uxX+uyY) *.25;
   gl_FragColor = t;
}