precision mediump float;

varying mediump vec2 v_TextureCoord;

uniform sampler2D u_Texture;
uniform vec2 singleStepOffset;
uniform mediump float params;

const highp vec3 W = vec3(0.299,0.587,0.114);
vec2 blurCoordinates[20];

float hardLight(float color)
{
	if(color <= 0.5)
		color = color * color * 2.0;
	else
		color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);
	return color;
}

void main(){

    vec3 centralColor = texture2D(u_Texture, v_TextureCoord).rgb;
    blurCoordinates[0] = v_TextureCoord.xy + singleStepOffset * vec2(0.0, -10.0);
    blurCoordinates[1] = v_TextureCoord.xy + singleStepOffset * vec2(0.0, 10.0);
    blurCoordinates[2] = v_TextureCoord.xy + singleStepOffset * vec2(-10.0, 0.0);
    blurCoordinates[3] = v_TextureCoord.xy + singleStepOffset * vec2(10.0, 0.0);
    blurCoordinates[4] = v_TextureCoord.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = v_TextureCoord.xy + singleStepOffset * vec2(5.0, 8.0);
    blurCoordinates[6] = v_TextureCoord.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[7] = v_TextureCoord.xy + singleStepOffset * vec2(-5.0, -8.0);
    blurCoordinates[8] = v_TextureCoord.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = v_TextureCoord.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = v_TextureCoord.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = v_TextureCoord.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = v_TextureCoord.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = v_TextureCoord.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = v_TextureCoord.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = v_TextureCoord.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = v_TextureCoord.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = v_TextureCoord.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = v_TextureCoord.xy + singleStepOffset * vec2(4.0, -4.0);
    blurCoordinates[19] = v_TextureCoord.xy + singleStepOffset * vec2(4.0, 4.0);

    float sampleColor = centralColor.g * 20.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[0]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[1]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[2]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[3]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[4]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[5]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[6]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[7]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[8]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[9]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[10]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[11]).g;
    sampleColor += texture2D(u_Texture, blurCoordinates[12]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[13]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[14]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[15]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[16]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[17]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[18]).g * 2.0;
    sampleColor += texture2D(u_Texture, blurCoordinates[19]).g * 2.0;

    sampleColor = sampleColor / 48.0;

    float highPass = centralColor.g - sampleColor + 0.5;

    for(int i = 0; i < 5;i++)
    {
        highPass = hardLight(highPass);
    }
    float luminance = dot(centralColor, W);

    float alpha = pow(luminance, params);

    vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;

    gl_FragColor = vec4(mix(smoothColor.rgb, max(smoothColor, centralColor), alpha), 1.0);
}