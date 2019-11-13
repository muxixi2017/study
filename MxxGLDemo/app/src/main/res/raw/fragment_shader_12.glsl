precision mediump float;

// varying mediump vec2 v_TextureCoord;

uniform sampler2D u_Texture;
uniform vec2 singleStepOffset;
uniform mediump float params;

 varying highp vec2 v_TextureCoord;
 varying highp vec2 leftTextureCoordinate;
 varying highp vec2 rightTextureCoordinate;
 varying highp vec2 topTextureCoordinate;
 varying highp vec2 bottomTextureCoordinate;

 varying highp float centerMultiplier;
 varying highp float edgeMultiplier;

 uniform sampler2D inputImageTexture;

 void main()
 {
     mediump vec3 textureColor = texture2D(u_Texture, v_TextureCoord).rgb;
     mediump vec3 leftTextureColor = texture2D(u_Texture, leftTextureCoordinate).rgb;
     mediump vec3 rightTextureColor = texture2D(u_Texture, rightTextureCoordinate).rgb;
     mediump vec3 topTextureColor = texture2D(u_Texture, topTextureCoordinate).rgb;
     mediump vec3 bottomTextureColor = texture2D(u_Texture, bottomTextureCoordinate).rgb;

     gl_FragColor = vec4((textureColor * centerMultiplier - (leftTextureColor * edgeMultiplier + rightTextureColor * edgeMultiplier + topTextureColor * edgeMultiplier + bottomTextureColor * edgeMultiplier)), texture2D(inputImageTexture, bottomTextureCoordinate).w);
 }