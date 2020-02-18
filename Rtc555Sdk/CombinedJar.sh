#/bin/bash
# Variables
OUTDIR=`pwd`/out
TMPOUTDIR=`pwd`/tmpout
TMPOUTDIRAAR=`pwd`/out/aar
BASEDIR=`pwd`
COMBINE=$TMPOUTDIR/combine
​
AARS=("@react-native-community_async-storage" "@react-native-community_netinfo" "android-jsc-intl-r245459" "react-native-cookies" "react-native-device-info" "react-native-incall-manager" "react-native-vector-icons" "react-native-webrtc" "react-native-webview" "react-native-0.59.9")
​
# for moving mediation adapter aars
function moveaar {
    NAME=${1//\//_}
    echoX 'moving aar for ---' $NAME
    cp $BASEDIR/$1/*.aar $TMPOUTDIR/$NAME.aar
    unzip -o $TMPOUTDIR/$NAME.aar -d $TMPOUTDIRAAR
    (cd $TMPOUTDIRAAR; jar xf $TMPOUTDIRAAR/classes.jar;)
}
​
function echoX {
    echo -e "BUILDSDKLOG: $@"
}
​
#####
# Execute
#####
echoX "Begin Building SDK AARs"
​
#####
# Prep
#####
rm -rf $OUTDIR
rm -rf $TMPOUTDIR
​
#####
# Copy AARs
#####
echoX "Copying -- AARs"
# Prep to move AARs
mkdir -p $COMBINE
mkdir -p $OUTDIR
​
#SDK AAR
cp Rtc555Sdk/build/outputs/aar/Rtc555Sdk-release.aar $TMPOUTDIR/sdk.aar
unzip -o $TMPOUTDIR/sdk.aar -d $TMPOUTDIRAAR
(cd $TMPOUTDIRAAR;
 echo "$TMPOUTDIRAAR"
 for entry in "$TMPOUTDIRAAR"/*
 do
  echo "$entry"
 done
 jar xf $TMPOUTDIRAAR/classes.jar;  )

 # move aars
 for i in ${!AARS[@]};
 do
 	moveaar ${AARS[$i]}
 done
​
# merge classes.jar files
echoX "Combining for classes.jar"
cd $TMPOUTDIRAAR
rm  classes.jar
jar cf $TMPOUTDIRAAR/classes.jar com*
rm -rf com*

SDKAAR=$OUTDIR/SDKAAR
rm -rf $SDKAAR
mkdir -p $SDKAAR
cd $SDKAAR
cp -Rf $TMPOUTDIRAAR/* $SDKAAR/
zip -r ../sdk-release.aar *
rm -rf $SDKAAR/
rm -rf $TMPOUTDIR
rm -rf $TMPOUTDIRAAR
