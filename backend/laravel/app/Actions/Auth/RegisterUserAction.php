<?php

namespace App\Actions\Auth;

use App\Models\User;

class RegisterUserAction
{
  /**
   * @param array<string, mixed> $data
   */
  public function execute(array $data): User
  {
    return User::query()->create([
      'name' => (string) $data['name'],
      'email' => (string) $data['email'],
      'password' => (string) $data['password'],
    ]);
  }
}
