precision mediump float;
  uniform sampler2D samp;
  varying vec2 tc;
  const float h = 1./512., dt = .001, tau = .5*dt/h;

vec4 getSampleSymmetric(sampler2D samp, vec2 base, vec2 coords) {
   float xfrac = fract(512.*coords.x);
   float yfrac = fract(512.*coords.y);
   float xint = floor(512.*coords.x)/512.;
   float yint = floor(512.*coords.y)/512.;
   vec4 nextul = texture2D(samp, base+vec2(xint+h,yint+h));
   vec4 nextur = texture2D(samp, base+vec2(xint,yint+h));
   vec4 nextll = texture2D(samp, base+vec2(xint+h,yint));
   vec4 nextlr = texture2D(samp, base+vec2(xint,yint));
   float center = min(2.*xfrac,min(2.*yfrac,min(2.-2.*xfrac,2.-2.*yfrac)))/4.;
   //float lr = max(1.-xfrac-yfrac,0.);
   //float ll = max(xfrac-yfrac,0.);
   float lr = max(1.-xfrac-yfrac,0.);
   float ll = max(xfrac-yfrac,0.);
   float ur = max(yfrac-xfrac,0.);
   float ul = max(xfrac+yfrac-1.,0.);
   vec4 av = (ul+center)*nextul+(ll+center)*nextll+(ur+center)*nextur+(lr+center)*nextlr;
   return av;
}

void main(void) {
   vec4 current = texture2D(samp, tc);
   vec2 D = -0.5*dt*current.rg;
   //vec2 Df = floor(D),   Dd = D - Df;
   //vec2 tc1 = tc + Df*h;
   //vec3 new =
   //  (texture2D(samp, tc1).rgb*(1. - Dd.g) +
   //   texture2D(samp, vec2(tc1.r, tc1.g + h)).rgb*Dd.g)*(1. - Dd.r) +
   //  (texture2D(samp, vec2(tc1.r + h, tc1.g)).rgb*(1. - Dd.g) +
   //   texture2D(samp, vec2(tc1.r + h, tc1.g + h)).rgb*Dd.g)*Dd.r;
   vec4 new = getSampleSymmetric(samp, tc, D);
   //vec4 new = texture2D(samp,tc+D);
   gl_FragColor = vec4(new.rgb,current.a);
   //gl_FragColor = current;
}