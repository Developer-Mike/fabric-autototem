{ pkgs ? import <nixpkgs> {} }:
let
  jdk = pkgs.jdk25;
in
pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk
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
    export JAVA_HOME=${jdk}
    export LD_LIBRARY_PATH="''${LD_LIBRARY_PATH}''${LD_LIBRARY_PATH:+:}${pkgs.libglvnd}/lib"

    # Fix for vscode-javac extension
    rm -f ~/.vscode/extensions/georgewfraser.vscode-javac-0.2.47/dist/linux/bin/java
    mkdir -p ~/.vscode/extensions/georgewfraser.vscode-javac-0.2.47/dist/linux/bin
    ln -s $(which java) ~/.vscode/extensions/georgewfraser.vscode-javac-0.2.47/dist/linux/bin/java
  '';
}