<?php

namespace App\Services;

use App\Actions\Auth\RegisterUserAction;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Models\User;
use Illuminate\Support\Facades\Auth;

class AuthService
{
  public function __construct(private readonly RegisterUserAction $registerUserAction)
  {
  }

  public function register(RegisterRequest $request): User
  {
    $user = $this->registerUserAction->execute($request->validated());

    Auth::guard('web')->login($user);

    if ($request->hasSession()) {
      $request->session()->regenerate();
    }

    return $user;
  }

  public function login(LoginRequest $request): ?User
  {
    $credentials = $request->only('email', 'password');
    $remember = (bool) $request->boolean('remember');

    if (!Auth::guard('web')->attempt($credentials, $remember)) {
      return null;
    }

    if ($request->hasSession()) {
      $request->session()->regenerate();
    }

    /** @var User $user */
    $user = Auth::guard('web')->user();

    return $user;
  }

  public function logout(): void
  {
    Auth::guard('web')->logout();
  }
}
