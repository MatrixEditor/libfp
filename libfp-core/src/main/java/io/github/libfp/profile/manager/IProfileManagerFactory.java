package io.github.libfp.profile.manager;//@date 30.10.2023

@FunctionalInterface
public interface IProfileManagerFactory
{

    ProfileManager newInstance();
}
