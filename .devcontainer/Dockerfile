# SPDX-FileCopyrightText: 2025 SUSE LLC
#
# SPDX-License-Identifier: Apache-2.0
FROM registry.opensuse.org/opensuse/tumbleweed:latest

# Installing all packages
RUN zypper refresh && \
        zypper -n install awk bash-completion curl expect git man openssh sudo tar vim wget \
                          nodejs24 java-25-openjdk-devel maven && \
         zypper clean -a

# Creating regular user
RUN useradd --create-home --shell /bin/bash develop
RUN echo "develop ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

USER develop
