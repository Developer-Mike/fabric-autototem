{ pkgs ? import <nixpkgs> {} }:

with pkgs;

mkShell {
  buildInputs = [
    jdk21
    xorg.libX11
    mesa
    libGL
    libglvnd
    glibc
    udev
  ];

  shellHook = ''
    export GRADLE_USER_HOME=$PWD/.gradle
    export GDK_BACKEND=x11
    export JAVA_HOME=${pkgs.jdk21}
    export LD_LIBRARY_PATH="''${LD_LIBRARY_PATH}''${LD_LIBRARY_PATH:+:}${pkgs.libglvnd}/lib"
  '';
}
