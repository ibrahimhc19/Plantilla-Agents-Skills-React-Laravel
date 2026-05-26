// npm install eslint-plugin-boundaries --save-dev

import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import unusedImports from "eslint-plugin-unused-imports";
import importPlugin from "eslint-plugin-import";
import perfectionist from "eslint-plugin-perfectionist";
import prettier from "eslint-config-prettier";

export default tseslint.config(
  { ignores: ["dist"] },

  {
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,
      prettier,
    ],
    files: ["**/*.{ts,tsx}"],

    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },

    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
      "unused-imports": unusedImports,
      import: importPlugin,
      perfectionist,
    },

    rules: {
      // =============================
      // React
      // =============================

      ...reactHooks.configs.recommended.rules,

      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],

      // =============================
      // Limpieza
      // =============================

      "unused-imports/no-unused-imports": "error",

      "@typescript-eslint/no-unused-vars": [
        "warn",
        { argsIgnorePattern: "^_", varsIgnorePattern: "^_" },
      ],

      "@typescript-eslint/no-explicit-any": "warn",

      // =============================
      // Arquitectura / calidad
      // =============================

      complexity: ["warn", 8],
      "max-lines-per-function": ["warn", 120],
      "max-lines": ["warn", 300],

      "no-nested-ternary": "warn",
      "no-unneeded-ternary": "warn",

      // =============================
      // Imports
      // =============================

      "import/order": [
        "warn",
        {
          groups: [
            "builtin",
            "external",
            "internal",
            ["parent", "sibling", "index"],
          ],
          pathGroups: [
            {
              pattern: "@/**",
              group: "internal",
              position: "before",
            },
          ],
          pathGroupsExcludedImportTypes: ["builtin"],
          "newlines-between": "always",
        },
      ],

      // IMPORTANTE: combinar reglas correctamente
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: ["../services/*"],
              message:
                "Do not import services directly across features. Use hooks instead.",
            },
            {
              group: ["../store/*"],
              message: "Avoid direct store access across unrelated features.",
            },
            {
              group: [
                "../../**/utils",
                "../../**/utils/*",
                "../../../**/utils",
                "../../../**/utils/*",
              ],
              message:
                "Prefer feature-level utils instead of deep cross-feature relative utils.",
            },
            {
              group: [
                "../../**/constants",
                "../../**/constants/*",
                "../../../**/constants",
                "../../../**/constants/*",
              ],
              message:
                "Prefer feature-level constants instead of deep cross-feature relative constants.",
            },
          ],
        },
      ],

      // =============================
      // Consistencia
      // =============================

      "perfectionist/sort-imports": [
        "error",
        {
          type: "natural",
          order: "asc",
        },
      ],

      // =============================
      // Buenas prácticas
      // =============================

      "no-console": ["warn", { allow: ["warn", "error"] }],

      "no-magic-numbers": [
        "warn",
        {
          ignore: [0, 1],
          enforceConst: true,
        },
      ],
    },
  },
);
