<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;
use Tests\TestCase;

class AuthApiTest extends TestCase
{
  use RefreshDatabase;

  public function test_user_can_register_from_api(): void
  {
    $response = $this->postJson('/api/auth/register', [
      'name' => 'Ada Lovelace',
      'email' => 'ada@example.com',
      'password' => 'password123',
      'password_confirmation' => 'password123',
    ]);

    $response
      ->assertCreated()
      ->assertJsonPath('data.email', 'ada@example.com');

    $this->assertDatabaseHas('users', [
      'email' => 'ada@example.com',
    ]);
  }

  public function test_user_can_login_with_valid_credentials(): void
  {
    User::factory()->create([
      'email' => 'grace@example.com',
      'password' => 'password123',
    ]);

    $response = $this->postJson('/api/auth/login', [
      'email' => 'grace@example.com',
      'password' => 'password123',
    ]);

    $response
      ->assertOk()
      ->assertJsonPath('data.email', 'grace@example.com');
  }

  public function test_authenticated_user_can_fetch_profile(): void
  {
    $user = User::factory()->create();

    Sanctum::actingAs($user);

    $response = $this->getJson('/api/auth/me');

    $response
      ->assertOk()
      ->assertJsonPath('data.id', $user->id);
  }
}
