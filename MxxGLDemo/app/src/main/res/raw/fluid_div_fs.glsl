precision mediump float;
  uniform sampler2D samp;
  varying vec2 tc;
  const float n = 512., h = 1./n;
void main(void) {
   vec4 t = texture2D(samp, tc);
   float left = texture2D(samp, tc+vec2(-h,0.)).a;
   float right = texture2D(samp, tc+vec2(h,0.)).a;
   float up = texture2D(samp, tc+vec2(0.,h)).a;
   float down = texture2D(samp, tc+vec2(0.,-h)).a;
   gl_FragColor = vec4(t.r+(right-left)/2.,t.g+(up-down)/2.,t.b,t.a);
}