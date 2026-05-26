<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Http\Resources\UserResource;
use App\Services\AuthService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class AuthController extends Controller
{
  public function __construct(private readonly AuthService $authService)
  {
  }

  public function register(RegisterRequest $request): JsonResponse
  {
    $user = $this->authService->register($request);

    return response()->json([
      'message' => 'User registered successfully.',
      'data' => new UserResource($user),
    ], Response::HTTP_CREATED);
  }

  public function login(LoginRequest $request): JsonResponse
  {
    $user = $this->authService->login($request);

    if (!$user) {
      return response()->json([
        'message' => 'Invalid credentials.',
      ], Response::HTTP_UNPROCESSABLE_ENTITY);
    }

    return response()->json([
      'message' => 'Authenticated successfully.',
      'data' => new UserResource($user),
    ]);
  }

  public function me(Request $request): JsonResponse
  {
    return response()->json([
      'data' => new UserResource($request->user()),
    ]);
  }

  public function logout(Request $request): JsonResponse
  {
    $this->authService->logout();

    if ($request->hasSession()) {
      $request->session()->invalidate();
      $request->session()->regenerateToken();
    }

    return response()->json([
      'message' => 'Logged out successfully.',
    ]);
  }
}
